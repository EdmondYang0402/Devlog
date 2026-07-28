package com.myproject.devlog.common;

import com.myproject.devlog.mapper.SiteConfigMapper;
import com.myproject.devlog.pojo.entity.SiteConfig;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.myproject.devlog.utils.UploadUrlUtil.isLocalUploadUrl;

@Component
public class SiteConfigValidator {
    public String normalizeRequired(String value, String message) {
        String normalized = trimToNull(value);

        if (normalized == null) {
            throw new BusinessException(message);
        }

        return normalized;
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

        if (keywords.size() > 8) {
            throw new BusinessException("展示关键词最多8个");
        }

        Set<String> normalized = new LinkedHashSet<>();

        for (String keyword : keywords) {
            String value = trimToNull(keyword);

            if (value == null) {
                throw new BusinessException("展示关键词不能为空");
            }

            if (value.length() > 30) {
                throw new BusinessException("每个展示关键词不能超过30个字符");
            }

            normalized.add(value);
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
