package dev.idriz.videomaker.controller.video;

import dev.idriz.videomaker.dto.ErrorResponse;
import dev.idriz.videomaker.service.AuthService;
import dev.idriz.videomaker.service.VideoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/video")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping("/order")
    public ResponseEntity<?> orderVideo(@RequestBody @Valid VideoOrderRequest request) {
        try {
            videoService.createVideo(
                    AuthService.getAuthenticatedUser(),
                    request.voice(),
                    request.title(),
                    request.promptLines().toArray(String[]::new)
            );
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to create video"));
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getOrders() {
        return ResponseEntity.ok(
                videoService.getVideos(AuthService.getAuthenticatedUser())
                        .stream()
                        .map(video -> new VideoDTO(
                                video.getTitle(),
                                video.getVoice(),
                                video.getGenerationPrompt(),
                                video.getUrl(),
                                video.isPending()
                        )).toList());
    }

    public record VideoOrderRequest(
            @NotNull List<@NotBlank @Size(min = 3, max = 32) String> promptLines,
            @NotBlank String title,
            @NotBlank String voice
    ) {
    }

    public record VideoDTO(
            String title,
            String voice,
            String generationPrompt,
            String url,
            boolean pending
    ) {
    }

}
