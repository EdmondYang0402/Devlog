package com.myproject.devlog.utils;

import com.myproject.devlog.common.BusinessException;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static com.myproject.devlog.utils.UploadUrlUtil.isLocalUploadUrl;

@Component
public class ProjectValidator {
    private static final int MAX_TECH_STACK_JSON_LENGTH = 1000;

    /** 必填与长度由 DTO 保证，这里只统一去除首尾空白。 */
    public String normalizeName(String name) {
        return name.strip();
    }

    /** 必填与长度由 DTO 保证，这里只统一去除首尾空白。 */
    public String normalizeSummary(String summary) {
        return summary.strip();
    }

    /** 详细介绍允许保存 Markdown 或富文本，只处理首尾空白，不清洗或解析内容。 */
    public String normalizeContent(String content) {
        return normalizeOptional(content);
    }

    /** 封面接受本站上传路径或 HTTP(S) URI，不访问网络。 */
    public String normalizeCoverUrl(String coverUrl) {
        String normalized = normalizeOptional(coverUrl);
        if (isLocalUploadUrl(normalized)) {
            return normalized;
        }
        return normalizeUrl(coverUrl, "项目封面地址不合法");
    }

    /**
     * 技术栈去除空项并按原顺序去重，限制条数可避免列表接口返回过度膨胀的数据。
     */
    public List<String> normalizeTechStack(List<String> techStack) {
        if (techStack == null || techStack.isEmpty()) {
            return List.of();
        }
        LinkedHashSet<String> normalized = new LinkedHashSet<>();
        for (String item : techStack) {
            if (item == null || item.isBlank()) {
                continue;
            }
            String value = item.strip();
            normalized.add(value);
        }
        return new ArrayList<>(normalized);
    }

    /** 序列化后再次核对数据库列容量，防止 JSON 转义造成长度膨胀。 */
    public void validateSerializedTechStack(String techStackJson) {
        if (techStackJson != null && techStackJson.length() > MAX_TECH_STACK_JSON_LENGTH) {
            throw new BusinessException("技术栈数据不能超过1000个字符");
        }
    }

    /** 代码仓库允许 GitHub、Gitee、GitLab 等任意 HTTP(S) 地址。 */
    public String normalizeGithubUrl(String githubUrl) {
        return normalizeUrl(githubUrl, "代码仓库地址不合法");
    }

    /** 在线演示地址只做格式校验，不探测远程服务是否可用。 */
    public String normalizeDemoUrl(String demoUrl) {
        return normalizeUrl(demoUrl, "在线演示地址不合法");
    }

    /** 合法值范围由 DTO 保证；这里只应用缺省值。 */
    public Integer normalizeFeatured(Integer featured) {
        return featured == null ? 0 : featured;
    }

    /** 合法值范围由 DTO 保证；这里只应用缺省值。 */
    public Integer normalizeSortOrder(Integer sortOrder) {
        return sortOrder == null ? 0 : sortOrder;
    }

    /** 起止日期均可为空；同时存在时仅保证完成日期不早于开始日期。 */
    public void validateDates(LocalDate startedDate, LocalDate completedDate) {
        if (startedDate != null && completedDate != null && completedDate.isBefore(startedDate)) {
            throw new BusinessException("完成日期不能早于开始日期");
        }
    }

    private String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.strip();
        return normalized.isEmpty() ? null : normalized;
    }

    private String normalizeUrl(String value, String errorMessage) {
        String normalized = normalizeOptional(value);
        if (normalized == null) {
            return null;
        }
        try {
            URI uri = new URI(normalized);
            String scheme = uri.getScheme();
            if (scheme == null
                    || !("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))
                    || uri.getHost() == null) {
                throw new BusinessException(errorMessage);
            }
        } catch (URISyntaxException exception) {
            throw new BusinessException(errorMessage);
        }
        return normalized;
    }
}
