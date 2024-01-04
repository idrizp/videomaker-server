package dev.idriz.videomaker.service;

import dev.idriz.videomaker.dto.OpenAIChatRequest;
import dev.idriz.videomaker.dto.OpenAIChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
public class LLMQueryService {

    private final String openaiSecretKey;

    private final RestClient restClient = RestClient.builder().build();

    public LLMQueryService(@Value("${secrets.openai_key}") String openaiSecretKey) {
        this.openaiSecretKey = openaiSecretKey;
    }

    public CompletableFuture<String> queryLLM(String systemPrompt, String prompt) {
        return CompletableFuture.supplyAsync(() -> {
            var request = OpenAIChatRequest.createRequest(systemPrompt, prompt);
            var response = Objects.requireNonNull(restClient.post()
                            .uri("https://api.openai.com/v1/chat/completions")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + openaiSecretKey)
                            .body(request)
                            .retrieve()
                            .body(OpenAIChatResponse.class))
                    .choices();
            if (response.isEmpty()) {
                throw new IllegalStateException("No response from OpenAI");
            }
            return response.getFirst().message().content();
        });
    }

}
