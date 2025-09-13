package com.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.image.*;
import org.springframework.ai.image.Image;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.ai.zhipuai.ZhiPuAiImageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class GenerateService {
    @Autowired
    private ZhiPuAiImageModel zhiPuAiImageModel;
    public Image generateImage(String image) {
        try {
            ImageOptions build = ImageOptionsBuilder.builder().width(512).N(1).height(512).build();
            ImagePrompt imagePrompt = new ImagePrompt(List.of(new ImageMessage(image)), build);
            ImageResponse call = zhiPuAiImageModel.call(imagePrompt);
            log.info("Call response: {}", call);
            return call.getResult().getOutput();
        }catch (Exception e){
            if(e instanceof NonTransientAiException){
                log.error("Non-transient ai exception", e);
                //预留一个余额不足的报警信息
            }else{
                throw e;
            }

        }
        return null;
    }
}
