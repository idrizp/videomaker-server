package dev.idriz.videomaker.service.video;

import dev.idriz.videomaker.video.FFMpeg;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class VideoGenerationService {

    private final ClipService clipService;

    public VideoGenerationService(
            ClipService clipService
    ) {
        this.clipService = clipService;
    }

    public CompletableFuture<String> generateVideo(String prompt, String voice) {
        String[] lines = prompt.split("\n");
        if (lines.length == 0) {
            return CompletableFuture
                    .failedFuture(new IllegalArgumentException("Prompt must have at least one line break."));
        }
        List<CompletableFuture<String>> snippets = new ArrayList<>(lines.length);
        for (String line : lines) {
            snippets.add(clipService.createClip(prompt, line, voice).thenApply(File::getAbsolutePath));
        }
        return CompletableFuture
                .allOf(snippets.toArray(new CompletableFuture[0]))
                .thenCompose(v -> {
                    try {
                        File output = File.createTempFile("videomaker", ".mp4", new File("/tmp"));
                        String[] paths = new String[snippets.size()];
                        for (int i = 0; i < snippets.size(); i++) {
                            paths[i] = snippets.get(i).join();
                        }
                        FFMpeg.runFFMpegCommand(FFMpeg.joinClips(paths).toString(), output.getAbsolutePath()).join();
                        for (String path : paths) {
                            new File(path).delete();
                        }
                        return CompletableFuture.completedFuture(output.getAbsolutePath());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

}
