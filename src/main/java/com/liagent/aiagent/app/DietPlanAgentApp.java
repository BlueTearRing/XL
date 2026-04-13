package com.liagent.aiagent.app;

import com.liagent.aiagent.advisor.MyLoggerAdvisor;
import com.liagent.aiagent.memory.ChatMemoryByMysql;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
public class DietPlanAgentApp {

    private final ChatClient chatClient;

    //引入所有工具
    @Resource
    private ToolCallback[] allTools;

    @Resource
    private Advisor dietPlanRagCloudAdvisor;

    @Resource
    private ToolCallbackProvider asyncToolCallbacks;

    public DietPlanAgentApp(ChatModel dashscopeChatModel, ChatMemoryByMysql chatMemoryByMysql) {
        // 使用数据库记忆替代内存记忆
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem("你是一个专业、细心且鼓励用户的营养与健康饮食规划助手。你的核心使命是基于营养学原理，为用户提供安全、实用、个性化且易于执行的饮食建议。")
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemoryByMysql),
                        new MyLoggerAdvisor()
                )
                .build();
    }

    /**
     * 基于同步实现
     * @param message 用户消息
     * @param chatId 聊天id
     * @return 整体结果
     */
    public String doChat(String message,String chatId){
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                //基于云知识库进行检索
                .advisors(dietPlanRagCloudAdvisor)
                .tools(allTools)
                .call()
                .chatResponse();
        String text = chatResponse.getResult().getOutput().getText();
        return text;
    }

    /**
     * 基于流式返回结果
     * @param message 用户消息
     * @param chatId 聊天id
     * @return 流式结果
     */
    public Flux<String> doChatFlux(String message,String chatId){
        Flux<String> content = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                //基于云知识库进行检索
                .advisors(dietPlanRagCloudAdvisor)
                .tools(allTools)
                .stream()
                .content();
        return content;
    }
}
