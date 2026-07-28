package com.myproject.devlog.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtil {

    private static final long EXPIRATION = 1000 * 60 * 60 * 24; // 1天
    private static final Key KEY = resolveKey();

    private JwtUtil() {
    }

    private static Key resolveKey() {
        String configuredSecret = System.getenv("JWT_SECRET");
        if (configuredSecret == null || configuredSecret.isBlank()) {
            return Keys.secretKeyFor(SignatureAlgorithm.HS256);
        }
        if (configuredSecret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT_SECRET must contain at least 32 bytes");
        }
        return Keys.hmacShaKeyFor(configuredSecret.getBytes(StandardCharsets.UTF_8));
    }

    public static String generateToken(Long userId, String sessionId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("sessionId", sessionId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Long parseUserId(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }

    public static String parseSessionId(String token) {
        return parseClaims(token).get("sessionId", String.class);
    }

    public static boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
