# spring-al-chat-example
Al的应用实现
项目介绍：spring-chat 简单的聊天模型的实现
        chat-memory 具有聊天记忆的模型实现
# spring-chat的介绍
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
# chat-memory 具有聊天记忆的本地模型应用的实现
环境准备：JDK17+ 、idea2024及以上版本、 Maven 3.6+ 、Spring Boot 3.3.4 、Spring AI 1.0.0、mysql8.0+
依赖准备：spring-ai-starter-model-chat-memory-repository-jdbc、spring-ai-starter-model-deepseek、spring-boot-starter-web、mysql-connector-j
## 主要的配置：
  spring:
  application:
    name: Spring-chatMemory
  datasource:
    url: jdbc:mysql://localhost:3306/al_chat?autoReconnect=true&useUnicode=true&characterEncoding=utf-8&autoReconnect=true
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      connection-init-sql: SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci        #确保每条 JDBC 连接都使用 utf8mb4 字符集并指定 utf8mb4_unicode_ci 排序
  ai:
    deepseek:
      api-key: sk-bda06bb0514a4adc8b33dfac2d26cc72
      base-url: https://api.deepseek.com
      chat:
        options:
          model: deepseek-chat
    chat:
      memory:
        repository:
          jdbc:
            platform: mariadb     #告诉jdbc框架当前使用的那个数据库
            initialize-schema: always #启动时，总是根据脚本去创建对应的数据库

server:
  port: 8081
## 代码实现
@Configuration
public class AiConfig {
    @Bean
    public ChatMemory chatMemory(JdbcChatMemoryRepository repo){
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(repo)
                .maxMessages(20)
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel, ChatMemory memory){
        ChatClient client = ChatClient.builder(chatModel)
                //设置 chatMemory
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(memory).build())
                .build();

        return client;

    }
}
@RestController
@RequestMapping("/ai")
public class ChatMemoryController {
    @Autowired
    private ChatClient chatClient;

    @GetMapping("/chatMemory")
    public String chatMemory(
            @RequestParam(value="cid") String conversationID,
            @RequestParam(value="message",defaultValue = "你是谁？") String message
    ){
        System.out.println("开始进行对话"+conversationID);
        String result = chatClient.prompt()
                .user(message)
                //加入聊天的会话
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationID))
                .call()
                .content();


        return result;
    }
}
