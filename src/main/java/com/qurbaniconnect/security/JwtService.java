package com.qurbaniconnect.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class JwtService {
    private final SecretKey key;
    private final long accessExpirationMs;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.access-expiration-ms}") long accessExpirationMs) {
        log.info("[Security] Initializing JwtService");
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            log.error("[Security] JWT secret is too short ({} characters), minimum 32 required", bytes.length);
            throw new IllegalStateException("JWT_SECRET must be at least 32 characters");
        }
        this.key = Keys.hmacShaKeyFor(bytes);
        this.accessExpirationMs = accessExpirationMs;
        log.info("[Security] JwtService initialized with access token expiration={}ms", accessExpirationMs);
    }

    public String generateAccessToken(Long userId) {
        log.debug("[Security] Generating access token for userId={}", userId);
        Date now = new Date();
        String token = Jwts.builder().subject(String.valueOf(userId)).issuedAt(now)
                .expiration(new Date(now.getTime() + accessExpirationMs)).signWith(key).compact();
        log.info("[Security] Access token generated for userId={}, expiresAt={}", userId, new Date(now.getTime() + accessExpirationMs));
        return token;
    }

    /** Returns the user id when the token is valid and unexpired. */
    public Optional<Long> validateAndGetUserId(String token) {
        log.debug("[Security] Validating JWT token");
        try {
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            Long userId = Long.parseLong(claims.getSubject());
            log.debug("[Security] Token validated successfully, userId={}, issuedAt={}, expiresAt={}", 
                    userId, claims.getIssuedAt(), claims.getExpiration());
            return Optional.of(userId);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("[Security] Token validation failed: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public long getAccessExpirationMs() { return accessExpirationMs; }
}
