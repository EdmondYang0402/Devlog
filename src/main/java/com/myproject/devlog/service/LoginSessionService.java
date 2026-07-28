package com.myproject.devlog.service;

public interface LoginSessionService {
    String createSession(Long userId);

    Long getUserId(String sessionId);

    void deleteSession(String sessionId);
}
