package dev.idriz.videomaker.video;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class FFMpeg {

    /**
     * Creates a clip mp4 from the given clip.
     *
     * @param clip the clip
     * @return a completable future
     */
    public static CompletableFuture<File> createClipVideo(Video.Clip clip) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                File outputFile = File.createTempFile("videomaker", ".mp4", new File("/tmp"));

                File downloadedVideo = Video.downloadVideo(clip.url());
                File downloadedAudio = Video.downloadAudio(clip.audioUrl());

                String arguments = replaceAudio(downloadedAudio.getAbsolutePath()) +
                        " " +
                        withVideoFilters(
                                withTextVideoFilter(clip.textSections().getFirst()).toString(),
                                "scale=1080:1920:force_original_aspect_ratio=decrease,pad=1080:1920:(ow-iw)/2:(oh-ih)/2"
                        ) + " -shortest";
                runFFMpegCommand(
                        downloadedVideo.getAbsolutePath(),
                        arguments,
                        outputFile.getAbsolutePath()
                ).join();

                downloadedVideo.delete();
                downloadedAudio.delete();
                return outputFile;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Runs the ffmpeg command with the given input, middle, and output.
     *
     * @param input  the input
     * @param middle the middle
     * @param output the output
     * @return a completable future
     */
    public static CompletableFuture<Void> runFFMpegCommand(String input, String middle, String output) {
        return runFFMpegCommand(String.format("-i %s %s", input, middle), output);
    }

    /**
     * Runs the ffmpeg command with the given arguments and output.
     *
     * @param arguments the arguments
     * @param output    the output
     * @return a completable future
     */
    public static CompletableFuture<Void> runFFMpegCommand(String arguments, String output) {
        return CompletableFuture.runAsync(() -> {
            try {
                String command = String.format("ffmpeg %s -y %s", arguments, output);
                System.out.println(command);
                ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
                processBuilder
                        .inheritIO()
                        .start()
                        .waitFor();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static StringBuilder replaceAudio(
            String audio
    ) {
        return new StringBuilder()
                .append("-i ").append(audio).append(" ")
                .append("-map 0:v:0 -map 1:a:0");
    }

    public static StringBuilder withVideoFilters(String... filters) {
        StringBuilder builder = new StringBuilder();
        builder.append("-vf ");
        for (String filter : filters) {
            builder.append("\"").append(filter).append("\"").append(",");
        }
        return builder;
    }

    public static StringBuilder withTextVideoFilter(
            Video.TextSection text
    ) {
        return new StringBuilder()
                .append("drawtext=text='").append(text.line()).append("':")
                .append("fontcolor=\"").append("white").append("\":")
                .append("fontsize=").append(text.fontSize()).append(":")
                .append("x=(w-text_w)/2").append(":")
                .append("y=(h-text_h)/2").append(":")
                .append("box=1:boxcolor=black@0.5:boxborderw=5");
    }

    public static StringBuilder withTrim(
            int start, int end
    ) {
        return new StringBuilder()
                .append("-ss ").append(start).append(" -to ").append(end);
    }

    public static StringBuilder joinClips(String[] videoClips) {
        StringBuilder commandBuilder = new StringBuilder();
        for (String clip : videoClips) {
            commandBuilder.append("-i ").append(clip).append(" ");
        }

        commandBuilder.append(String.format("-filter_complex \"[0:v][0:a][1:v][1:a]concat=n=%d:v=1:a=1\" -fps_mode vfr",
                videoClips.length));
        return commandBuilder;
    }
}
