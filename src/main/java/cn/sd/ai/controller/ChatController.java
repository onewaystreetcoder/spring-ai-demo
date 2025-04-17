package cn.sd.ai.controller;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

@Controller
public class ChatController {
    private final OllamaChatModel chatModel;

    @Autowired
    public ChatController(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
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
}
