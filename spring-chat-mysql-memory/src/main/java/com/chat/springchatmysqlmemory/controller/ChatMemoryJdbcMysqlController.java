package com.chat.springchatmysqlmemory.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatMemoryJdbcMysqlController {
    @Autowired
    private ChatClient chatClient;
    @GetMapping("chatMemory")
    public String chatMemory(@RequestParam(value = "message" ,defaultValue = "介绍一下你是谁") String message,
                             @RequestParam(value = "cid",required = false) String cid) {
        String content = chatClient.prompt().user(message).advisors(advisors -> {
            advisors.param(ChatMemory.CONVERSATION_ID, cid);
        }).call().content();
        return content;

    }
}
