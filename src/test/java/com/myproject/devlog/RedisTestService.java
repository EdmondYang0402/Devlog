package com.myproject.devlog;

import com.myproject.devlog.service.LoginSessionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RedisTestService {
    @Autowired
    private LoginSessionService loginSessionService;

    @Test
    void createAndGet() {
        Long userId = 10L;

        String sessionId =
                loginSessionService.createSession(userId);

        Long result =
                loginSessionService.getUserId(sessionId);

        assertEquals(userId, result);

        loginSessionService.deleteSession(sessionId);
    }

}
