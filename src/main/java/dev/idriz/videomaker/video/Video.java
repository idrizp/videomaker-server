package dev.idriz.videomaker.video;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class Video {

    private final List<Clip> clips = new ArrayList<>();
    private final List<TextSection> textSections = new ArrayList<>();

    /**
     * Downloads the video from the given url and returns the file.
     *
     * @param url the url of the video
     * @return the file of the video
     */
    public static File downloadVideo(String url) {
        try (InputStream inputStream = new URL(url).openStream()) {
            Path file = Files.createTempFile(Path.of("/tmp"), "videomaker", ".mp4");
            Files.copy(inputStream, file, StandardCopyOption.REPLACE_EXISTING);
            return file.toFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static File downloadAudio(String url) {
        try (InputStream inputStream = new URL(url).openStream()) {
            Path file = Files.createTempFile(Path.of("/tmp"), "videomaker", ".wav");
            Files.copy(inputStream, file, StandardCopyOption.REPLACE_EXISTING);
            return file.toFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Video addClip(Clip clip) {
        clips.add(clip);
        return this;
    }

    public List<Clip> getClips() {
        return clips;
    }

    public List<TextSection> getTextSections() {
        return textSections;
    }

    public record Clip(
            String url,
            String audioUrl,
            List<TextSection> textSections
    ) {
    }

    public record TextSection(
            String line,
            String fontFamily,
            int fontSize,
            int x,
            int y,
            int width,
            int height,
            Color color,
            double start,
            double end
    ) {
        public static double computeTimeToRead(String line) {
            return line.length() / 8d;
        }
    }


}
