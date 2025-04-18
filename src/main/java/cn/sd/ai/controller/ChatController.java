package cn.sd.ai.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.sd.ai.service.EmbeddingService;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final OllamaChatModel chatModel;
    private final OllamaApi ollamaApi;
    private final EmbeddingService embeddingService;
    @Autowired
    public ChatController(OllamaChatModel chatModel, OllamaApi ollamaApi, EmbeddingService embeddingService) {
        this.chatModel = chatModel;
        this.ollamaApi = ollamaApi;
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
        ChatResponse response = this.chatModel.call(new Prompt(userMessage, OllamaOptions.builder()
                .toolNames("getSqlResultTool").build()));
        return Map.of("generation", response.getResult().getOutput().getText());
    }

    @PostMapping("/tool_chat2")
    @ResponseBody
    public Map<String,String> toolChat2(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        OllamaApi.ChatRequest ollamaRequest = OllamaApi.ChatRequest.builder(chatModel.getDefaultOptions().getModel())
                .messages(List.of(
                        OllamaApi.Message.builder(OllamaApi.Message.Role.SYSTEM)
                                .content("You are a helpful assistant.").build(),
                        OllamaApi.Message.builder(OllamaApi.Message.Role.USER)
                                .content(message).build()
                        ))
                .tools(List.of(new OllamaApi.ChatRequest.Tool(new OllamaApi.ChatRequest.Tool.Function("Execute SQL to return query results", "getSqlResultTool", "{\"sql\": \"string\"}"))))
                .build();
        OllamaApi.ChatResponse response = ollamaApi.chat(ollamaRequest);

        return Map.of("generation", response.message().content());
    }

    @PostMapping("/tool_chat3")
    @ResponseBody
    public Map<String,String> toolChat3(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        SystemMessage systemMessage = new SystemMessage("You are a helpful assistant.");
        UserMessage userMessage = new UserMessage(message);
        List<Message> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.add(userMessage);


        List<Document> documents = embeddingService.searchDocument(message);
        if (CollUtil.isNotEmpty(documents)) {
            Document document = documents.get(0);
            String schemas = (String)document.getMetadata().get("schemas");
            if (StrUtil.isNotEmpty(schemas)) {
                AssistantMessage assistantMessage = new AssistantMessage(schemas);
                messages.add(assistantMessage);
            }
        }

        Prompt prompt = new Prompt(messages, OllamaOptions.builder().toolNames("getSqlResultTool").build());

        ChatResponse response = this.chatModel.call(prompt);
        return Map.of("generation", response.getResult().getOutput().getText());
    }
}
