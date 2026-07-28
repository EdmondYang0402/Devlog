package com.myproject.devlog.utils;

import com.myproject.devlog.common.BusinessException;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

import static com.myproject.devlog.utils.UploadUrlUtil.isLocalUploadUrl;

@Component
public class SiteBackgroundValidator {
    private static final int MIN_SORT_ORDER = -100000;
    private static final int MAX_SORT_ORDER = 100000;

    /** 接受本站上传路径或 HTTP(S) 地址，不访问任何远程资源。 */
    public String normalizeImageUrl(String imageUrl) {
        if (imageUrl == null) {
            throw new BusinessException("背景图片地址不能为空");
        }
        String normalized = imageUrl.strip();
        if (normalized.isEmpty()) {
            throw new BusinessException("背景图片地址不能为空");
        }
        if (normalized.length() > 500) {
            throw new BusinessException("背景图片地址不能超过500个字符");
        }
        if (isLocalUploadUrl(normalized)) {
            return normalized;
        }
        try {
            URI uri = new URI(normalized);
            String scheme = uri.getScheme();
            if (scheme == null
                    || !("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))
                    || uri.getHost() == null) {
                throw new BusinessException("背景图片地址不合法");
            }
        } catch (URISyntaxException exception) {
            throw new BusinessException("背景图片地址不合法");
        }
        return normalized;
    }

    /** 后台识别名称可省略，空白名称统一保存为 null。 */
    public String normalizeTitle(String title) {
        if (title == null) {
            return null;
        }
        String normalized = title.strip();
        if (normalized.isEmpty()) {
            return null;
        }
        if (normalized.length() > 100) {
            throw new BusinessException("背景图片名称不能超过100个字符");
        }
        return normalized;
    }

    /** 新建记录默认启用，同时只允许数据库约定的 0 或 1。 */
    public Integer normalizeEnabled(Integer enabled) {
        int normalized = enabled == null ? 1 : enabled;
        if (normalized != 0 && normalized != 1) {
            throw new BusinessException("启用状态不合法");
        }
        return normalized;
    }

    /** 排序权重越大越靠前，并限制在便于后台维护的简单整数范围。 */
    public Integer normalizeSortOrder(Integer sortOrder) {
        int normalized = sortOrder == null ? 0 : sortOrder;
        if (normalized < MIN_SORT_ORDER || normalized > MAX_SORT_ORDER) {
            throw new BusinessException("背景图片排序权重不合法");
        }
        return normalized;
    }
}
