package com.chat.controller;

import com.chat.service.RagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/rag")
public class RagController {
    @Autowired
    private RagService ragService;
    @GetMapping("/ask")
    public Map<String,String> ask(@RequestParam(value = "message") String message) {
        String resp = ragService.query(message);
        return Map.of("question",message,"answer",resp);
    }
}
