package dev.idriz.videomaker.dto;

import java.util.List;

public record OpenAIChatResponse(
        String model,
        List<Choice> choices
) {
    public record Choice(Message message) {
        public record Message(String content, String role) {}
    }
}
