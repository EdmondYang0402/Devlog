package com.myproject.devlog.service.impl;

import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.service.LoginSessionService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;

import java.time.Duration;
import java.util.UUID;

@Service
public class LoginSessionServiceImpl implements LoginSessionService {

    private final StringRedisTemplate stringRedisTemplate;

    public LoginSessionServiceImpl(
            StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    private static final String SESSION_KEY_PREFIX =
            "devlog:auth:session:";

    private String buildKey(String sessionId) {
        return SESSION_KEY_PREFIX + sessionId;
    }

    @Override
    public String createSession(Long userId) {
        if (userId == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "用户 ID 不能为空");
        }

        String sessionId = UUID.randomUUID().toString();
        String key = buildKey(sessionId);

        stringRedisTemplate.opsForValue().set(
                key,
                userId.toString(),
                Duration.ofHours(24)
        );

        return sessionId;
    }

    @Override
    public Long getUserId(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return null;
        }

        String value = stringRedisTemplate
                .opsForValue()
                .get(buildKey(sessionId));

        if (value == null) {
            return null;
        }

        return Long.valueOf(value);
    }

    @Override
    public void deleteSession(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return;
        }

        stringRedisTemplate.delete(buildKey(sessionId));
    }
}
