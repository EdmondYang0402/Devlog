package com.myproject.devlog.common;

public class UserContext {

    private static final ThreadLocal<Long> USER = new ThreadLocal<>();

    public static void set(Long userId) {
        USER.set(userId);
    }

    public static Long get() {
        return USER.get();
    }

    public static void clear() {
        USER.remove();
    }
}
