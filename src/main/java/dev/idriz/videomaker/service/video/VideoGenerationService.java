package dev.idriz.videomaker.service.video;

import dev.idriz.videomaker.entity.AppUser;
import dev.idriz.videomaker.entity.Video;
import dev.idriz.videomaker.repository.ClipRepository;
import dev.idriz.videomaker.repository.VideoRepository;
import dev.idriz.videomaker.service.BalanceService;
import dev.idriz.videomaker.service.StorageService;
import dev.idriz.videomaker.video.FFMpeg;
import dev.idriz.videomaker.video.VideoUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class VideoGenerationService {

    private final ClipRepository clipRepository;
    private final ClipService clipService;
    private final VideoRepository videoRepository;
    private final StorageService storageService;
    private final BalanceService balanceService;

    public VideoGenerationService(
            ClipRepository clipRepository,
            ClipService clipService,
            VideoRepository videoRepository,
            StorageService storageService,
            BalanceService balanceService
    ) {
        this.clipRepository = clipRepository;
        this.clipService = clipService;
        this.videoRepository = videoRepository;
        this.storageService = storageService;
        this.balanceService = balanceService;
    }

    public CompletableFuture<Video> generateVideo(AppUser appUser, String prompt, String title, String voice) {
        String[] lines = prompt.split("\n");
        Video video = new Video();
        video.setGenerationPrompt(prompt);
        video.setTitle(title);
        video.setCost(balanceService.getCost(lines));
        video.setGenerationStart(System.currentTimeMillis());
        video.setClips(new ArrayList<>());
        video.setVoice(voice);

        if (lines.length == 0) {
            return CompletableFuture
                    .failedFuture(new IllegalArgumentException("Prompt must have at least one line break."));
        }
        List<CompletableFuture<VideoUtils.Clip>> snippets = new ArrayList<>(lines.length);
        for (String line : lines) {
            snippets.add(clipService.createClip(prompt, line, voice));
        }
        return CompletableFuture
                .allOf(snippets.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    try {
                        File output = File.createTempFile("videomaker", ".mp4", new File("/tmp"));
                        String[] paths = new String[snippets.size()];
                        for (int i = 0; i < snippets.size(); i++) {
                            var clip = snippets.get(i).join();
                            paths[i] = snippets.get(i).join().getFilePath();

                            var fileUrl = storageService.upload(clip.getFilePath()).join();
                            var audioUrl = storageService.upload(clip.getAudioFilePath()).join();

                            Video.Clip clipEntity = new Video.Clip();
                            clipEntity.setOrdinal(i);

                            clipEntity.setVideo(video);
                            clipEntity.setTextSection(clip.getTextSections().getFirst());

                            clipEntity.setAudioUrl(audioUrl);
                            clipEntity.setUrl(fileUrl);

                            video.getClips().add(clipEntity);
                        }
                        // Save all the clips into the database.
                        FFMpeg.runFFMpegCommand(FFMpeg.joinClips(paths).toString(), output.getAbsolutePath()).join();
                        for (String path : paths) {
                            new File(path).delete();
                        }

                        storageService.uploadAndDelete(output.getAbsolutePath()).join();

                        video.setUrl(output.getName());
                        video.setUser(appUser);
                        video.setGenerationEnd(System.currentTimeMillis());

                        videoRepository.save(video);
                        clipRepository.saveAll(video.getClips());
                        return video;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

}
