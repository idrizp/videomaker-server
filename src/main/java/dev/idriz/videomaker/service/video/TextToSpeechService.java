package dev.idriz.videomaker.service.video;

import dev.idriz.videomaker.dto.TextToSpeechAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class TextToSpeechService {

    private final RestClient restClient = RestClient.builder().build();
    private final String replicateSecretKey;
    private final String webhookUrl;
    private final Map<String, CompletableFuture<String>> futures = new HashMap<>();

    public TextToSpeechService(@Value("${secrets.replicate_key}") String replicateSecretKey, @Value("${webhooks.replicate}") String webhookUrl) {
        this.replicateSecretKey = replicateSecretKey;
        this.webhookUrl = webhookUrl;
    }

    /**
     * This is used to complete the future that was created when the request was made to the TTS API.
     *
     * @param id    The id of the request
     * @param audio The audio returned from the TTS API
     */
    public void populateReturnedAudio(String id, String audio) {
        CompletableFuture<String> future = futures.remove(id);
        if (future != null) {
            if (audio == null) {
                future.completeExceptionally(new RuntimeException("TTS failed"));
                return;
            }
            future.complete(audio);
        }
    }


    public CompletableFuture<String> useLocalTTS(String text, String voice) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                File output = File.createTempFile("videomaker", ".wav", new File("/tmp"));
//                output.deleteOnExit();
                ProcessBuilder processBuilder = new ProcessBuilder();
                String command = String.format("tts --text \"%s\" --model_name \"%s\" --out_path %s",
                        text,
                        "tts_models/multilingual/multi-dataset/xtts_v2",
                        output.getAbsolutePath()
                );
                processBuilder.command("bash", "-c", command);
                processBuilder.start().waitFor();
                return output.getAbsolutePath();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<String> useTTS(
            String text,
            String voice
    ) {
        return CompletableFuture.supplyAsync(() -> {
                    var responseBody = restClient.post()
                            .uri("https://api.replicate.com/v1/predictions")
                            .header("Authorization", "Token " + replicateSecretKey)
                            .header("Content-Type", "application/json")
                            .body(TextToSpeechAPI.createRequest(text, voice, webhookUrl))
                            .retrieve().body(TextToSpeechAPI.Response.class);
                    if (responseBody == null || responseBody.id() == null) {
                        throw new RuntimeException("Failed to generate audio");
                    }
                    return responseBody;
                })
                .thenApply(
                        TextToSpeechAPI.Response::id
                )
                .thenCompose((id) -> {
                    CompletableFuture<String> future = new CompletableFuture<>();
                    futures.put(id, future);
                    return future;
                })
                .orTimeout(3, TimeUnit.MINUTES);
    }

}
