# spring-al-chat-example
Al的应用实现
项目介绍：Spring Al的聊天模型的实现demo
环境准备：JDK17+ 、idea2024及以上版本、 Maven 3.6+ 、Spring Boot 3.3.4 、Spring AI 1.0.0
您需要使用 DeepSeek 创建 API 密钥才能访问 DeepSeek 语言模型。
依赖：spring-boot-starter-web: For creating RESTful web services
spring-ai-starter-model-deepseek : To integrate Spring AI with OpenAI
spring-boot-starter-test: For testing purposes
申请api-key的操作：
在DeepSeek 开放平台页面创建一个帐户，并在API Keys 页面生成一个令牌。
主要的启动代码：
  @SpringBootApplication
public class SpringChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringChatApplication.class, args);
    }

}
调用大模型的主要方法：
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

    @GetMapping("/ai/generateStream")
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        var prompt = new Prompt(new UserMessage(message));
        return chatModel.stream(prompt);
    }

}

更详细的操作流程请参考博客：https://blog.csdn.net/weixin_47068446/article/details/149838157?spm=1001.2014.3001.5501
