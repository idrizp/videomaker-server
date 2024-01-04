package dev.idriz.videomaker.dto;


import java.util.List;

public record OpenAIChatRequest(String model, List<Message> messages){
    public record Message(String role, String content) {}

    public static OpenAIChatRequest createRequest(String systemPrompt, String prompt) {
        return new OpenAIChatRequest("gpt-3.5-turbo", List.of(
                new Message("system", systemPrompt),
                new Message("user", prompt)
        ));
    }
}

