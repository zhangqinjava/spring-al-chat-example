package com.chat.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;

@Slf4j
@RestController
@RequestMapping("/image")
public class ImageController {
    private ChatClient zhipuChatClient;
    public ImageController(@Qualifier("zhiPuChatClient") ChatClient zhipuChatClient) {
        this.zhipuChatClient = zhipuChatClient;
    }
    @GetMapping("/analyImage")
    public String analyImage() {
        log.info("开始进行图片识别");
        ClassPathResource classPathResource = new ClassPathResource("img_1.png");
        return zhipuChatClient.prompt("图片识别").user(c->{
            c.text("请对图片进行描述").media(MediaType.IMAGE_PNG, classPathResource);
        }).call().content();
    }


}
