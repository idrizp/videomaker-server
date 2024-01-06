package dev.idriz.videomaker.video;

import jakarta.persistence.Embeddable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class VideoUtils {

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

    public static final class Clip {

        private String filePath;
        private String audioFilePath;
        private List<TextSection> textSections;

        public Clip(
                String filePath,
                String audioFilePath,
                List<TextSection> textSections
        ) {
            this.filePath = filePath;
            this.audioFilePath = audioFilePath;
            this.textSections = textSections;
        }

        public String getFilePath() {
            return filePath;
        }

        public String getAudioFilePath() {
            return audioFilePath;
        }

        public String getFileName() {
            return filePath.substring(filePath.lastIndexOf("/") + 1);
        }

        public String getAudioFileName() {
            return audioFilePath.substring(audioFilePath.lastIndexOf("/") + 1);
        }

        public List<TextSection> getTextSections() {
            return textSections;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public void setAudioFilePath(String audioFilePath) {
            this.audioFilePath = audioFilePath;
        }

        public void setTextSections(List<TextSection> textSections) {
            this.textSections = textSections;
        }
    }

    @Embeddable
    public static class TextSection {

        private String line;
        private String fontFamily;
        private int fontSize;
        private int x;
        private int y;
        private int width;
        private int height;
        private String hexColor;
        private double startTime;
        private double endTime;

        public TextSection(
                String line,
                String fontFamily,
                int fontSize,
                int x,
                int y,
                int width,
                int height,
                String hexColor,
                double startTime,
                double endTime
        ) {
            this.line = line;
            this.fontFamily = fontFamily;
            this.fontSize = fontSize;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.hexColor = hexColor;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public TextSection() {

        }

        public static double computeTimeToRead(String line) {
            return line.length() / 8d;
        }

        public String line() {
            return line;
        }

        public String fontFamily() {
            return fontFamily;
        }

        public int fontSize() {
            return fontSize;
        }

        public int x() {
            return x;
        }

        public int y() {
            return y;
        }

        public int width() {
            return width;
        }

        public int height() {
            return height;
        }

        public String hexColor() {
            return hexColor;
        }

        public double start() {
            return startTime;
        }

        public double end() {
            return endTime;
        }
    }
}
