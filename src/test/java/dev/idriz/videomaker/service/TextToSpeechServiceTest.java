package dev.idriz.videomaker.service;

import dev.idriz.videomaker.service.video.TextToSpeechService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;

@WebMvcTest
@ActiveProfiles("dev")
public class TextToSpeechServiceTest {

    private final String replicateSecretKey;
    private final TextToSpeechService ttsService;

    public TextToSpeechServiceTest(@Value("${secrets.replicate_key}") String replicateSecretKey,
                                   @Value("${webhooks.replicate}") String webhookUrl) {
        this.replicateSecretKey = replicateSecretKey;
        this.ttsService = new TextToSpeechService(replicateSecretKey, webhookUrl);
    }

    @Test
    void useTTS() {
        System.out.println(ttsService.useTTS("Hello, world!", "").join());
    }

}
