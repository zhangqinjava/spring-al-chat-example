package org.al.chatmemory.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class ChatMemoryController {
    @Autowired
    private ChatClient chatClient;

    @GetMapping("/chatMemory")
    public String chatMemory(
            @RequestParam(value="cid") String conversationID,
            @RequestParam(value="message",defaultValue = "你是谁？") String message
    ){
        System.out.println("开始进行对话"+conversationID);
        String result = chatClient.prompt()
                .user(message)
                //加入聊天的会话
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationID))
                .call()
                .content();


        return result;
    }
}
