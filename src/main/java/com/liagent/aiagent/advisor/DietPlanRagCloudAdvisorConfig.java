package com.liagent.aiagent.advisor;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 云知识库配置
 */
@Configuration
@Slf4j
public class DietPlanRagCloudAdvisorConfig {

    @Value("${spring.ai.dashscope.api-key}")
    private String dashscopeApiKey;

    private final String KNOWLEDGE_INDEX = "饮食规划";

    @Bean
    public Advisor dietPlanRagCloudAdvisor() {
        // 初始化DashScope API客户端
        DashScopeApi dashScopeApi = new DashScopeApi(dashscopeApiKey);

        // 创建文档检索器，配置使用指定的知识索引
        DocumentRetriever documentRetriever = new DashScopeDocumentRetriever(dashScopeApi,
                DashScopeDocumentRetrieverOptions.builder()
                        .withIndexName(KNOWLEDGE_INDEX)  // 设置知识索引名称
                        .build());

        // 构建并返回检索增强生成顾问
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)  // 注入文档检索器
                .build();

    }

}
