package com.chat.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class RagService {
    private EmbeddingModel embeddingModel;
    private ChatClient chatClient;
    private List<String> docs=new ArrayList<>();
    private List<float[]> vectors=new ArrayList<>();
    public RagService(EmbeddingModel embeddingModel, ChatClient.Builder chatClient) throws Exception {
        this.embeddingModel = embeddingModel;
        this.chatClient = chatClient.build();
        ClassPathResource resource = new ClassPathResource("古代诗歌常用意象.txt");
         String str =new String( resource.getInputStream().readAllBytes() , StandardCharsets.UTF_8);
        String[] split = str.split("----");
        for (String string : split) {
            System.out.println(">>>>>>>>>>>>>>>:"+string);
            docs.add(string);
            vectors.add(embeddingModel.embed(string));
        }


    }

    public String query(String message) {
        //对用户提问内容向量化
        float[] qv = embeddingModel.embed(message);

        //定义 top2 相似度值
        double v1 = -1;
        double v2 = -1;

        //定义 相似度最大的两个向量对应的文档下标
        int index1 = -1;
        int index2 = -1;

        //将向量化内容与知识库中各个向量进行相似度比对，获取最相似top2
        for(int i=0;i<vectors.size();i++){

            //两个向量相似度计算
            double sim = cosineSimilarity(qv, vectors.get(i));
            if(sim > v1){
                //赋值相似度
                v2=v1;
                v1=sim;

                //赋值 下标
                index2=index1;
                index1=i;

            }else if(sim >v2){
                v2=sim;
                index2 = i;
            }

        }

        //获取top2 最相似chunk 内容，拼接在一起作为上下文/prompt 提供给LLM
        String ctx = "";
        if(index1!=-1){
            ctx = docs.get(index1) + (index2 >= 0 ? "\n----\n" + docs.get(index2) : "");
        }

        //构建回复
        //准备prompt
        String prompt = "以下是知识库内容：\n"+ctx+"\n 请基于上述知识库内容回答用户问题："+message;

        //将获取到的top2 文档作为提示词交给 chat大模型 回复
        ChatClient.CallResponseSpec resp = chatClient.prompt()
                .system("你是知识助手，结合上下文回答用户问题")
                .user(prompt)
                .call();


        //获取模型返回的内容
        return resp.content();
    }
    //两个向量余弦相似度计算
    private double cosineSimilarity(float[] a, float[] b) {
        double dot = 0, na = 0, nb = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            na += a[i] * a[i];
            nb += b[i] * b[i];
        }
        return dot / (Math.sqrt(na) * Math.sqrt(nb));
    }
}
