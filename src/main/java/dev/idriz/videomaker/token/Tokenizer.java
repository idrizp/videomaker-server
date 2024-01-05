package dev.idriz.videomaker.token;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.EncodingType;
import com.knuddels.jtokkit.api.ModelType;

import java.util.List;

public class Tokenizer {

    private static final EncodingRegistry ENCODING_REGISTRY = Encodings.newDefaultEncodingRegistry();
    private static final Encoding ENCODING = ENCODING_REGISTRY.getEncodingForModel(ModelType.GPT_3_5_TURBO);

    public static List<Integer> encode(String text) {
        return ENCODING.encode(text);
    }

    public static String decoded(List<Integer> tokens) {
        return ENCODING.decode(tokens);
    }



}
