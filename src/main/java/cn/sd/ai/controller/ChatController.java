package cn.sd.ai.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.sd.ai.service.EmbeddingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class ChatController {
    public static final Logger logger = LoggerFactory.getLogger(EmbeddingController.class);
    private final ChatModel chatModel;
    private final EmbeddingService embeddingService;
    @Value("${systemPrompt}")
    private String systemPrompt_;


    @Autowired
    public ChatController(ChatModel chatModel, EmbeddingService embeddingService) {
        this.chatModel = chatModel;
        this.embeddingService = embeddingService;
    }

    @PostMapping("/chat")
    @ResponseBody
    public Map<String,String> chat(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return Map.of("generation", this.chatModel.call(message));
    }

    @PostMapping("/stream_chat")
    @ResponseBody
    public Flux<ChatResponse> streamChat(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return this.chatModel.stream(prompt);
    }


    @PostMapping("/tool_chat")
    @ResponseBody
    public Map<String,String> toolChat(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {

        UserMessage userMessage = new UserMessage(message);
        ChatResponse response = this.chatModel.call(new Prompt(userMessage, ToolCallingChatOptions.builder()
                .toolNames("getSqlResultTool").build()));
        return Map.of("generation", response.getResult().getOutput().getText());
    }

//    @PostMapping("/tool_chat2")
//    @ResponseBody
//    public Map<String,String> toolChat2(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
//        OllamaApi.ChatRequest ollamaRequest = OllamaApi.ChatRequest.builder(chatModel.getDefaultOptions().getModel())
//                .messages(List.of(
//                        OllamaApi.Message.builder(OllamaApi.Message.Role.SYSTEM)
//                                .content("You are a helpful assistant.").build(),
//                        OllamaApi.Message.builder(OllamaApi.Message.Role.USER)
//                                .content(message).build()
//                        ))
//                .tools(List.of(new OllamaApi.ChatRequest.Tool(new OllamaApi.ChatRequest.Tool.Function("Execute SQL to return query results", "getSqlResult", "{\"sql\": \"string\"}"))))
//                .build();
//        OllamaApi.ChatResponse response = ollamaApi.chat(ollamaRequest);
//
//        return Map.of("generation", response.message().content());
//    }

    @PostMapping("/tool_chat4")
    @ResponseBody
    public Map<String,String> toolChat4(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message,
                                        @RequestParam(value = "usingKnowledge", defaultValue = "false") String usingKnowledge,
                                        @RequestParam(value = "score", defaultValue = "0.5") double score,
                                        @RequestParam(value = "systemPrompt", defaultValue = "") String systemPrompt
                                        ) {
        if (StrUtil.isEmpty(systemPrompt)) {
            systemPrompt =  this.systemPrompt_;
        }
        if ("true".equals(usingKnowledge)) {
            List<Document> documents = embeddingService.searchDocument(message);
            if (CollUtil.isNotEmpty(documents)) {
                Document document = documents.get(0);
                String schemas = (String)document.getMetadata().get("schemas");
                if (StrUtil.isNotEmpty(schemas) && null != document.getScore() && document.getScore() > score){
                    //将schemas添加到messages中时不调用工具，不知道为啥
                    AssistantMessage assistantMessage = new AssistantMessage(schemas);
//                messages.add(assistantMessage);
//                    systemPrompt = systemPrompt + "\n\n" + schemas;
                    systemPrompt = StrUtil.format(systemPrompt, schemas);
                }
            }
        }


        SystemMessage systemMessage = new SystemMessage(systemPrompt);
        UserMessage userMessage = new UserMessage(message);
        List<Message> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.add(userMessage);

        Prompt prompt = new Prompt(messages, ToolCallingChatOptions.builder().toolNames("getSqlResult").build());

        ChatResponse response = this.chatModel.call(prompt);
        String text = response.getResult().getOutput().getText();
        logger.info("toolChat3 response: {}", text);

        return Map.of("generation", StrUtil.removeSuffix(StrUtil.removePrefix(text,"\""), "\""));
    }

    @PostMapping("/tool_chat3")
    @ResponseBody
    public Map<String,String> toolChat3(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message,
                                        @RequestParam(value = "usingKnowledge", defaultValue = "false") String usingKnowledge,
                                        @RequestParam(value = "score", defaultValue = "0.5") double score
    ) {
        String systemMessageStr = "你是一位公司数据库专家，对公司数据表很熟悉，能够快速准确的根据问题写出对应SQL,要求所生成的SQL表名、字段要在给出的表结构中，并调用工具getSqlResultTool获取结果,将结果整理整markdown格式展示";

        if ("true".equals(usingKnowledge)) {
            List<Document> documents = embeddingService.searchDocument(message);
            if (CollUtil.isNotEmpty(documents)) {
                Document document = documents.get(0);
                String schemas = (String)document.getMetadata().get("schemas");
                if (StrUtil.isNotEmpty(schemas) && null != document.getScore() && document.getScore() > score){
                    //将schemas添加到messages中时不调用工具，不知道为啥
                    AssistantMessage assistantMessage = new AssistantMessage(schemas);
//                messages.add(assistantMessage);
                    systemMessageStr = systemMessageStr + "\n\n" + schemas;
                }
            }
        }


        SystemMessage systemMessage = new SystemMessage(systemMessageStr);
        UserMessage userMessage = new UserMessage(message);
        List<Message> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.add(userMessage);

        Prompt prompt = new Prompt(messages, ToolCallingChatOptions.builder().toolNames("getSqlResult").build());

        ChatResponse response = this.chatModel.call(prompt);
        String text = response.getResult().getOutput().getText();
        logger.info("toolChat3 response: {}", text);

        return Map.of("generation", StrUtil.removeSuffix(StrUtil.removePrefix(text,"\""), "\""));
    }
}
