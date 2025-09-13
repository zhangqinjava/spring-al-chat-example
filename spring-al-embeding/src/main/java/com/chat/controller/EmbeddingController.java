package com.chat.controller;

import com.chat.service.EmbedingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
@RestController
@RequestMapping("/ai")
@Slf4j
public class EmbeddingController {
    @Autowired
    private EmbeddingModel embeddingModel;
    @Autowired
    private EmbedingService embedingService;
    @GetMapping("/embedding")
    public Map embeding(@RequestParam(value = "message",defaultValue = "给我讲个笑话") String message) {
        log.info("message is:{}", message);
        float[] embed = embeddingModel.embed(message);
        return Map.of("embed", embed, "message", message);

    }
    @GetMapping("/similarity")
    public String similarity(@RequestParam(value = "message") String message) {
        return embedingService.query(message);

    }

}
