package dev.idriz.videomaker.token;

import dev.idriz.videomaker.entity.AppUser;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JWT {

    private final SecretKey secretKey;
    private final long accessTokenValidity = 1000 * 60 * 60 * 24 * 7; // 7 days

    private final JwtParser jwtParser;

    public JWT(@Value("${secrets.jwt}") String secret) {

        byte[] bytes = Decoders.BASE64.decode(secret);
        SecretKey key = Keys.hmacShaKeyFor(bytes);

        this.secretKey = key;

        this.jwtParser = Jwts.parser().verifyWith(key).build();
    }

    /**
     * Creates a token for the user
     *
     * @param appUser the user
     * @return the token
     */
    public String createToken(AppUser appUser) {
        return Jwts.builder()
                .subject(appUser.getId().toString())
                .issuedAt(new Date())
                .expiration(new java.util.Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Extracts the user id from the token
     *
     * @param token the token
     * @return the user id or null if the token is invalid
     */
    public String extractUserId(String token) {
        try {
            return jwtParser.parseSignedClaims(token).getPayload().getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}
