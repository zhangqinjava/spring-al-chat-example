package com.chat.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
@Slf4j
public class MultiModelController {
    private ChatClient zhiPuChatClient;
    private ChatClient deepSeekCharClient;
    public MultiModelController(@Qualifier("zhiPuChatClient") ChatClient zhiPuChatClient,@Qualifier("deepSeekChatClient") ChatClient deepSeekCharClient){
        this.zhiPuChatClient = zhiPuChatClient;
        this.deepSeekCharClient = deepSeekCharClient;
    }

    @GetMapping("/deepchat")
    public String deepseekChat(@RequestParam(value = "message") String message) {
        log.info("deepchat的问题:{}",message);
        return deepSeekCharClient.prompt().user(message).call().content();
    }
    @GetMapping("/zhipuchat")
    public String zhiPuChat(@RequestParam(value = "message") String message) {
        log.info("zhipuchat的问题:{}",message);
        return zhiPuChatClient.prompt().user(message).call().content();
    }
}
