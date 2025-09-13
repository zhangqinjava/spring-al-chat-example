package com.chat.controller;

import com.chat.service.GenerateService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.image.Image;
import org.springframework.ai.zhipuai.ZhiPuAiImageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/ai")
public class ImageController {
    @Autowired
    private GenerateService generateService;
    @GetMapping("/image")
    public void image(@RequestParam(value = "message",defaultValue = "画一只小狗") String message,
                      HttpServletResponse response) throws Exception {
        Image image = generateService.generateImage(message);
        response.sendRedirect(response.encodeRedirectURL(image.getUrl()));
    }
}
