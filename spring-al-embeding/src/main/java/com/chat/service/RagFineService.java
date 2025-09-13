package com.chat.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class RagFineService {
    List<String> docs=new ArrayList<>();
    List<float[]> vectors=new ArrayList<>();
    private EmbeddingModel embeddingModel;
    private ChatClient chatClient;
    public RagFineService(EmbeddingModel embeddingModel, ChatClient.Builder chatClient) {
        this.embeddingModel = embeddingModel;
        this.chatClient = chatClient.build();
        ClassPathResource classPathResource = new ClassPathResource("古代诗歌常用意象.txt");
        TextReader textReader = new TextReader(classPathResource);
        List<Document> read = textReader.read();
        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(800) //拆分chunk 每个最多 800 token
                .withMinChunkSizeChars(400) //每个chunk 最小允许的字符
                .withKeepSeparator(true)//保留分隔符，提高上下文连贯性
                .build();
        List<Document> chunks = splitter.apply(read);
        for (Document chunk : chunks) {
            String strip = chunk.getText().strip();
            docs.add(strip);
            vectors.add(embeddingModel.embed(strip));
        }

    }
    class IndexSim {
        int index;//文档索引
        double sim;//相似度

        public IndexSim(int index, double sim) {
            this.index = index;
            this.sim = sim;
        }
    }
    public String query(String message) {
        float[] embed = embeddingModel.embed(message);
        int k=5;
        double threadold=0.05;
        List<IndexSim> indexSims = new ArrayList<>();
        for (int i = 0; i < vectors.size(); i++) {
            double sim=cosineSimilarity(embed, vectors.get(i));
            if (sim>threadold) {
                indexSims.add(new IndexSim(i, sim));
            }
        }
        indexSims.sort((o1, o2) -> Double.compare(o2.index, o1.index));
        List<IndexSim> collect = indexSims.stream().limit(k).collect(Collectors.toList());
        TreeSet<Integer> set = new TreeSet();
        for (IndexSim indexSim : collect) {
            set.add(indexSim.index);
            if (indexSim.index-1>=0){
                set.add(indexSim.index-1);
            }
            if (indexSim.index+1<=vectors.size()-1){
                set.add(indexSim.index+1);
            }
        }
        String context = set.stream().map(integer ->
            docs.get(integer)
        ).collect(Collectors.joining("\n---\n"));
        String prompt = "以下是知识库内容：\n"+context+"\n 请基于上述知识库内容回答用户问题："+message;
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
