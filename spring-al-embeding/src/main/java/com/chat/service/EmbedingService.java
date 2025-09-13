package com.chat.service;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmbedingService {
    private EmbeddingModel embeddingModel;
    private List<float[]> docVertos;
    private List<String> docs=List.of(
            "美食非常美味，服务员也很友好",
            "这部电影既刺激又令人兴奋",
            "阅读书籍是扩展知识的好方法");
    public EmbedingService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
        this.docVertos=this.embeddingModel.embed(docs);
    }
    public String query(String message) {
        //1.对用户传入的 query 进行向量化
        float[] embed = embeddingModel.embed(message);
        double bastSim = -1;//记录目前最大的相似度
        int bastIdx = -1;//记录与当前输入文本最相似文本的下标
        //2.遍历 docVertors 来与用户传入的文本向量进行计算相似度，找出最相似一个返回
        for (int i = 0; i < docVertos.size(); i++) {
            double v = cosineSimilarity(embed, docVertos.get(i));
            if(v>bastSim){
                bastSim=v;
                bastIdx=i;
            }
        }
        return docs.get(bastIdx);


    }
    private double cosineSimilarity(float[] a, float[] b) {
        double dot = 0, na = 0, nb = 0; // 初始化点积和向量模长的平方
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];    // 计算点积
            na += a[i] * a[i];     // 计算向量a的模长平方
            nb += b[i] * b[i];     // 计算向量b的模长平方
        }
        double denominator = Math.sqrt(na) * Math.sqrt(nb); // 计算分母
        return denominator != 0 ? dot / denominator : 0; // 避免除零错误
    }
}
