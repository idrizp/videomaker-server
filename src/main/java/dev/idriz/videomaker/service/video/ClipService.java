package dev.idriz.videomaker.service.video;

import dev.idriz.videomaker.list.ListUtils;
import dev.idriz.videomaker.service.LLMQueryService;
import dev.idriz.videomaker.video.FFMpeg;
import dev.idriz.videomaker.video.Video;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ClipService {

    private final LLMQueryService queryService;
    private final StockVideoService stockVideoService;
    private final TextToSpeechService textToSpeechService;

    public ClipService(LLMQueryService queryService, StockVideoService stockVideoService, TextToSpeechService textToSpeechService) {
        this.queryService = queryService;
        this.stockVideoService = stockVideoService;
        this.textToSpeechService = textToSpeechService;
    }

    public CompletableFuture<String> getSuitableStockVideoForText(String prompt, String text) {
        return queryService.queryLLM(
                """
                        Return tags for the line provided according to the context. The context is given below.
                        Do not return anything else except the tags. All tags must be lowercase. The tags must be separated by commas.
                        At most 2 tags may be returned. Always orient the tag to the main context, in every line. 
                        At least one tag must be fully related to the main context.
                        
                        Context:
                        """ + prompt,
                text
        ).thenCompose(query -> {
            System.out.println(query);
            var videos = stockVideoService.getVideos(query, 1);
            if (videos.isEmpty()) {
                throw new IllegalStateException("No videos found for query: " + query);
            }
            var video = ListUtils.getRandomElement(videos);
            if (video == null) {
                throw new IllegalStateException("No videos found for query: " + query);
            }
            if (video.videoFiles() == null) {
                throw new IllegalStateException("No videos found for query: " + query);
            }
            var videoFile = video.videoFiles()
                    .stream()
                    .filter(file -> file.quality().equalsIgnoreCase("hd") && file.fileType().endsWith("mp4"))
                    .findFirst()
                    .orElse(null);
            if (videoFile == null) {
                throw new IllegalStateException("No HD video found for query: " + query);
            }
            return CompletableFuture.completedFuture(videoFile.link());
        });
    }

    public CompletableFuture<File> createClip(String prompt, String line, String voice) {
        return getSuitableStockVideoForText(prompt, line).thenCompose(url -> {
            var audio = textToSpeechService.useTTS(line, voice).join();
            var clip = new Video.Clip(url, audio, List.of(new Video.TextSection(line,
                    "Arial",
                    20,
                    0,
                    0,
                    0,
                    0,
                    Color.WHITE,
                    0,
                    Video.TextSection.computeTimeToRead(line)
            )));
            var file = FFMpeg.createClipVideo(clip).join();
            return CompletableFuture.completedFuture(file);
        });
    }

}
