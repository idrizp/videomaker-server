package dev.idriz.videomaker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TextToSpeechAPI {

    public static Request createRequest(String text, String voice, String webhookUrl) {
        return new Request(
                new RequestBody(
                        text,
                        voice,
                        0.0,
                        true,
                        0.33,
                        0.33
                ),
                "ff5bcc71dc2c44662291fc348b9ca2eb40107c9f4b377b169fc0dea950c388c8",
                webhookUrl
        );
    }

    public record Request(RequestBody input, String version, String webhook) {
    }

    public record RequestBody(
            @JsonProperty("input_text") String inputText,
            @JsonProperty("target_voice") String targetVoice,
            @JsonProperty("denoise_ratio") double denoiseRatio,
            @JsonProperty("scale_output_volume") boolean scaleOutputVolume,
            @JsonProperty("text_to_vector_temperature") double textToVectorTemperature,
            @JsonProperty("voice_conversion_temperature") double voiceConversionTemperature
    ) {
    }

    public record Response(
            @JsonProperty("output") String output,
            @JsonProperty("status") String status,
            @JsonProperty("metrics") Metrics metrics,
            @JsonProperty("started_at") String startedAt,
            @JsonProperty("id") String id
    ) {
        public record Metrics(long predictTime) {
        }
    }

}
