package com.myproject.devlog.utils;

import com.myproject.devlog.common.BusinessException;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

import static com.myproject.devlog.utils.UploadUrlUtil.isLocalUploadUrl;

@Component
public class SiteBackgroundValidator {
    /** 必填与长度由 DTO 保证；这里保留规范化和 URL 协议语义校验。 */
    public String normalizeImageUrl(String imageUrl) {
        String normalized = imageUrl.strip();
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
        return normalized;
    }

    /** 合法值范围由 DTO 保证；这里只应用缺省值。 */
    public Integer normalizeEnabled(Integer enabled) {
        return enabled == null ? 1 : enabled;
    }

    /** 合法值范围由 DTO 保证；这里只应用缺省值。 */
    public Integer normalizeSortOrder(Integer sortOrder) {
        return sortOrder == null ? 0 : sortOrder;
    }
}
