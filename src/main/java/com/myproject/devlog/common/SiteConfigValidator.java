package com.myproject.devlog.common;

import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.myproject.devlog.utils.UploadUrlUtil.isLocalUploadUrl;

@Component
public class SiteConfigValidator {
    public String normalizeRequired(String value) {
        return value.trim();
    }

    public String normalizeOptional(String value) {
        return trimToNull(value);
    }

    public String normalizeUrl(String value, String message) {
        String url = trimToNull(value);

        if (url == null) {
            return null;
        }
        if (isLocalUploadUrl(url)) {
            return url;
        }

        try {
            URI uri = URI.create(url);

            boolean validScheme =
                    "http".equalsIgnoreCase(uri.getScheme())
                            || "https".equalsIgnoreCase(uri.getScheme());

            if (validScheme && uri.getHost() != null) {
                return url;
            }
        } catch (IllegalArgumentException ignored) {
        }

        throw new BusinessException(message);
    }

    public List<String> normalizeKeywords(List<String> keywords) {
        if (keywords == null) {
            return List.of();
        }

        Set<String> normalized = new LinkedHashSet<>();

        for (String keyword : keywords) {
            normalized.add(keyword.trim());
        }

        return new ArrayList<>(normalized);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }


}
