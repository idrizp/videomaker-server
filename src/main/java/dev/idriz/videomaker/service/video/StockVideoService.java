package dev.idriz.videomaker.service.video;

import dev.idriz.videomaker.dto.PexelsAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.File;
import java.util.List;

@Service
public class StockVideoService {

    private final RestClient restClient = RestClient.builder().build();
    private final String pexelsSecretKey;

    public StockVideoService(@Value("${secrets.pexels_key}") String pexelsSecretKey) {
        this.pexelsSecretKey = pexelsSecretKey;
    }

    public List<PexelsAPI.Video> getVideos(String query, int page) {
        PexelsAPI.VideoListResponse response = restClient.get()
                .uri("https://api.pexels.com/videos/search?query=" + query + "?page=" + page + "?per_page=3?size=medium?orientation=portrait")
                .header("Authorization", pexelsSecretKey)
                .retrieve()
                .body(PexelsAPI.VideoListResponse.class);
        if (response == null) {
            throw new IllegalStateException("No response from Pexels");
        }
        return response.videos();
    }

}
