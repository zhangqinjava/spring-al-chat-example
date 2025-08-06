package org.al.springchat.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
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

    /**
     * 流式写法
     * @param message
     * @return
     */
    @GetMapping("/ai/generateStream")
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        var prompt = new Prompt(new UserMessage(message));
        return chatModel.stream(prompt);
    }
    @GetMapping("/ai/runtimeOptions")
    public String runtimeOptions(
            @RequestParam(value = "message",defaultValue = "你是谁？") String message,
            @RequestParam(value = "temp" ,required = false) Double temp){

        System.out.println("收到消息："+message+",temp = "+temp);

        Prompt prompt ;
        if(temp !=null){
            System.out.println("使用传入的temp...");
            DeepSeekChatOptions build = DeepSeekChatOptions.builder()
                    .temperature(temp)
                    .build();

            prompt = new Prompt(message, build);
        }else{
            System.out.println("使用默认的 temp...");
            prompt = new Prompt(message);
        }

        ChatResponse resp = chatModel.call(prompt);

        return resp.getResult().getOutput().getText();

    }

    /**
     * 渐进式流式输出
     * @param message
     * @param response
     * @return
     */
    @GetMapping("/ai/generateStream2")
    public Flux<String> generateStream2(
            @RequestParam(value = "message",defaultValue = "你是谁？") String message,
            HttpServletResponse response
    ) {
        //设置字符为utf-8编码，避免乱码
        response.setCharacterEncoding("UTF-8");
        System.out.println("message = "+message);
        //与模型对话，流式返回内容
        Prompt prompt = new Prompt(new UserMessage(message));
        Flux<ChatResponse> stream = chatModel.stream(prompt);

        Flux<String> resp = stream.map(
                chatResponse -> chatResponse.getResult().getOutput().getText()
        );
        System.out.println("返回内容："+resp);
        return resp;

    }
}
