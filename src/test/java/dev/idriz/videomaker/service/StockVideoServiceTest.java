package dev.idriz.videomaker.service;

import dev.idriz.videomaker.service.video.StockVideoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest
@ActiveProfiles("dev")
public class StockVideoServiceTest {

    private final StockVideoService stockVideoService;
    StockVideoServiceTest(final @Value("${secrets.pexels_key}") String pexelsKey) {
        this.stockVideoService = new StockVideoService(pexelsKey);
    }

    @Test
    void getVideos() {
        assertFalse(stockVideoService.getVideos("cat", 1).isEmpty());
    }

}
