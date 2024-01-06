package dev.idriz.videomaker.service;

import dev.idriz.videomaker.entity.AppUser;
import dev.idriz.videomaker.entity.Video;
import dev.idriz.videomaker.list.Pair;
import dev.idriz.videomaker.repository.VideoRepository;
import dev.idriz.videomaker.service.video.VideoGenerationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class VideoService {

    private final VideoGenerationService videoGenerationService;
    private final VideoRepository videoRepository;
    private final MailService mailService;
    private final BalanceService balanceService;

    public VideoService(
            VideoGenerationService videoGenerationService,
            VideoRepository videoRepository,
            MailService mailService,
            BalanceService balanceService
    ) {
        this.videoGenerationService = videoGenerationService;
        this.videoRepository = videoRepository;
        this.mailService = mailService;
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
    public CompletableFuture<Pair<VideoCreationResult, Video>> createVideo(AppUser appUser, String voice, String title, String... promptLines) {
        var affordable = balanceService.canAfford(appUser, promptLines);
        if (!affordable) {
            return CompletableFuture.completedFuture(new Pair<>(VideoCreationResult.LOW_BALANCE, null));
        }
        var withdrawn = balanceService.withdrawFromBalance(appUser.getId(), balanceService.getCost(promptLines));
        if (!withdrawn) {
            return CompletableFuture.completedFuture(new Pair<>(VideoCreationResult.LOW_BALANCE, null));
        }
        return videoGenerationService
                .generateVideo(String.join("\n", promptLines), title, voice)
                .thenApply(result -> {
                    // TODO: Mail the user the video
                    if (result == null) {
                        mailService.sendMail(
                                appUser.getEmail(),
                                "Your video failed to generate!",
                                """
                                        Your video failed to generate! This shouldn't happen(contact a developer), and please try again later!
                                        In the meanwhile, you've been refunded for the video :)!
                                        """);
                        balanceService.addToBalance(appUser.getId(), balanceService.getCost(promptLines));
                        return new Pair<>(VideoCreationResult.FAILED, null);
                    }
                    mailService.sendMail(
                            appUser.getEmail(),
                            "Your video is ready!",
                            String.format("Your video is ready! You can view and edit it at https://videomaker.app/video/%s",
                                    result.getId().toString())
                    );
                    return new Pair<>(VideoCreationResult.SUCCESS, result);
                });
    }

    public List<Video> getVideos(AppUser authenticatedUser) {
        return videoRepository.findVideosByUserId(authenticatedUser.getId());
    }

    public enum VideoCreationResult {
        FAILED,
        LOW_BALANCE,
        SUCCESS
    }

}
