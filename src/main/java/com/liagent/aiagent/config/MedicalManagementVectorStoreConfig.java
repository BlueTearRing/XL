package com.liagent.aiagent.config;

import com.liagent.aiagent.loader.MedicalDocumentLoader;
import jakarta.annotation.Resource;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 初始化向量数据库bean
 */
@Configuration
public class MedicalManagementVectorStoreConfig {

    @Resource
    private MedicalDocumentLoader documentLoader;

    @Bean
    VectorStore MedicalManagementVectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        vectorStore.add(documentLoader.loadMarkdowns());
        return vectorStore;
    }
}
