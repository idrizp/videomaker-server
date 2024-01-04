package dev.idriz.videomaker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PexelsAPI {

    public record VideoListResponse(int page, int perPage, int totalResults, String url, List<Video> videos) {
    }

    public record VideoFile(int id, String quality, @JsonProperty("file_type") String fileType, int width,
                            int height, String link) {

    }

    public record Video(int id, int width, int height, String url, String image, int duration,
                        @JsonProperty("video_files") List<VideoFile> videoFiles) {
    }

}
