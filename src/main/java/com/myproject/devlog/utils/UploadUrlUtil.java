package com.myproject.devlog.utils;

import java.net.URI;

public final class UploadUrlUtil {
    private UploadUrlUtil() {
    }

    public static boolean isLocalUploadUrl(String value) {
        if (value == null || !value.startsWith("/uploads/") || value.contains("..")) {
            return false;
        }
        try {
            URI uri = URI.create(value);
            return !uri.isAbsolute()
                    && uri.getHost() == null
                    && uri.getQuery() == null
                    && uri.getFragment() == null
                    && uri.getPath() != null
                    && uri.getPath().startsWith("/uploads/");
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
}
