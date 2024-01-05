package dev.idriz.videomaker.service;

import dev.idriz.videomaker.entity.AppUser;
import dev.idriz.videomaker.repository.VideoRepository;
import dev.idriz.videomaker.service.video.VideoGenerationService;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class VideoService {

    private final VideoGenerationService videoGenerationService;
    private final VideoRepository videoRepository;
    private final BalanceService balanceService;

    public VideoService(VideoGenerationService videoGenerationService, VideoRepository videoRepository, BalanceService balanceService) {
        this.videoGenerationService = videoGenerationService;
        this.videoRepository = videoRepository;
        this.balanceService = balanceService;
    }

    /**
     * Creates a video for the given user with the given voice, title and prompt lines
     *
     * @param appUser     the user to create the video for
     * @param voice       the voice to use
     * @param title       the title of the video
     * @param promptLines the prompt lines to use
     * @return a CompletableFuture that will complete with the result of the video creation
     */
    public CompletableFuture<VideoCreationResult> createVideo(AppUser appUser, String voice, String title, String... promptLines) {
        var affordable = balanceService.canAfford(appUser, promptLines);
        if (!affordable) {
            return CompletableFuture.completedFuture(VideoCreationResult.LOW_BALANCE);
        }
        var withdrawn = balanceService.withdrawFromBalance(appUser.getId(), balanceService.getCost(promptLines));
        if (!withdrawn) {
            return CompletableFuture.completedFuture(VideoCreationResult.LOW_BALANCE);
        }
        return videoGenerationService
                .generateVideo(String.join("\n", promptLines), title, voice)
                .thenApply(result -> {
                    // TODO: Mail the user the video
                    if (result == null) {
                        return VideoCreationResult.FAILED;
                    }
                    return VideoCreationResult.SUCCESS;
                });
    }

    public enum VideoCreationResult {
        FAILED,
        LOW_BALANCE,
        SUCCESS
    }

}
