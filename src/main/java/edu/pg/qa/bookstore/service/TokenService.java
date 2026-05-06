package edu.pg.qa.bookstore.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {

    private static final long TOKEN_TTL_SECONDS = 3600;

    private static class TokenInfo {
        private final String token;
        private final Instant expiresAt;

        private TokenInfo(String token, Instant expiresAt) {
            this.token = token;
            this.expiresAt = expiresAt;
        }
    }

    private final Map<String, TokenInfo> tokens = new ConcurrentHashMap<>();

    public String issueTokenForUser(String username) {
        String token = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plusSeconds(TOKEN_TTL_SECONDS);
        tokens.put(token, new TokenInfo(token, expiresAt));
        return token;
    }

    public boolean isTokenValid(String token) {
        TokenInfo info = tokens.get(token);
        if (info == null) {
            return false;
        }
        if (info.expiresAt.isBefore(Instant.now())) {
            tokens.remove(token);
            return false;
        }
        return true;
    }

    public long getTokenTtlSeconds() {
        return TOKEN_TTL_SECONDS;
    }
}