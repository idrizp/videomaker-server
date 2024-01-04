package dev.idriz.videomaker.service;

import org.antlr.v4.runtime.misc.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest
@ActiveProfiles("dev")
class LLMQueryServiceTest {

    private final LLMQueryService queryService;

    LLMQueryServiceTest(final @Value("${secrets.openai_key}") String openaiKey) {
        this.queryService = new LLMQueryService(openaiKey);
    }

    @Test
    void queryLLM() {
        var systemPrompt = "You only respond with YES or NO and nothing else. In uppercase.";
        var prompt = "Are you working?";
        var response = queryService.queryLLM(systemPrompt, prompt).join();
        assertNotNull(response);
        assertTrue(response.equals("YES") || response.equals("NO"));
    }
}