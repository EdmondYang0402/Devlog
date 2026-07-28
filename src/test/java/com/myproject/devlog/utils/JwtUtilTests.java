package com.myproject.devlog.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilTests {

    @Test
    void tokenContainsUserIdAndSessionId() {
        Long userId = 42L;
        String sessionId = "test-session-id";

        String token = JwtUtil.generateToken(userId, sessionId);

        assertTrue(JwtUtil.validateToken(token));
        assertEquals(userId, JwtUtil.parseUserId(token));
        assertEquals(sessionId, JwtUtil.parseSessionId(token));
    }
}
