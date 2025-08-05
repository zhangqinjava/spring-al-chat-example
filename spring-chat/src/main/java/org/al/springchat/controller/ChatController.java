package org.al.springchat.controller;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
public class ChatController {
    public final DeepSeekChatModel chatModel;


    public ChatController(DeepSeekChatModel deepSeekChatModel) {
        this.chatModel = deepSeekChatModel;
    }
    @GetMapping("/ai/generate")
    public Map generate(@RequestParam(value="message",defaultValue="介绍一下自己")String message) {
        System.out.println(message);
        return Map.of("generation",chatModel.call(message));
    }

    @GetMapping("/ai/generateStream")
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        var prompt = new Prompt(new UserMessage(message));
        return chatModel.stream(prompt);
    }

}
