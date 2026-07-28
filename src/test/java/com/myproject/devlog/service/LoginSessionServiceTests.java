package com.myproject.devlog.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class LoginSessionServiceTests {

    @Autowired
    private LoginSessionService loginSessionService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void createsReadsAndDeletesSession() {
        Long userId = 42L;

        String sessionId = loginSessionService.createSession(userId);
        assertEquals(userId, loginSessionService.getUserId(sessionId));
        Long ttlSeconds = stringRedisTemplate.getExpire(
                "devlog:auth:session:" + sessionId,
                java.util.concurrent.TimeUnit.SECONDS
        );
        assertTrue(ttlSeconds != null
                && ttlSeconds <= Duration.ofHours(24).toSeconds()
                && ttlSeconds > Duration.ofHours(23).toSeconds());

        loginSessionService.deleteSession(sessionId);
        assertNull(loginSessionService.getUserId(sessionId));
    }
}
