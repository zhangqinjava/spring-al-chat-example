package org.al.chatmemory.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(ChatModel chatModel, ChatMemory memory){
        ChatClient client = ChatClient.builder(chatModel)
                //设置 chatMemory
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(memory).build())
                .build();

        return client;

    }
}
