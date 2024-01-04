package dev.idriz.videomaker.controller.webhook;

import dev.idriz.videomaker.dto.TextToSpeechAPI;
import dev.idriz.videomaker.service.video.TextToSpeechService;
import dev.idriz.videomaker.service.video.VideoGenerationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook/tts")
public class TTSWebhookController {

    private final TextToSpeechService textToSpeechService;
    private final VideoGenerationService videoGenerationService;

    public TTSWebhookController(TextToSpeechService textToSpeechService, VideoGenerationService videoGenerationService) {
        this.textToSpeechService = textToSpeechService;
        this.videoGenerationService = videoGenerationService;
    }

    @PostMapping
    public void ttsWebhook(@RequestBody TextToSpeechAPI.Response response) {
        if (!response.status().equalsIgnoreCase("succeeded")) {
            textToSpeechService.populateReturnedAudio(response.id(), null);
            return;
        }
        textToSpeechService.populateReturnedAudio(response.id(), response.output());
    }

}
