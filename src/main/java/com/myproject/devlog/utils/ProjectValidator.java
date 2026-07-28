package com.myproject.devlog.utils;

import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.common.ProjectStatusConstant;
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
    private static final int MAX_TECH_STACK_ITEMS = 20;
    private static final int MAX_TECH_STACK_ITEM_LENGTH = 50;
    private static final int MAX_TECH_STACK_JSON_LENGTH = 1000;
    private static final int MIN_SORT_ORDER = -100000;
    private static final int MAX_SORT_ORDER = 100000;

    /** 规范化项目名称，并保证数据库必填字段满足 100 字符上限。 */
    public String normalizeName(String name) {
        return normalizeRequired(name, 100, "项目名称不能为空", "项目名称不能超过100个字符");
    }

    /** 规范化一句话简介，并保证数据库必填字段满足 300 字符上限。 */
    public String normalizeSummary(String summary) {
        return normalizeRequired(summary, 300, "项目简介不能为空", "项目简介不能超过300个字符");
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
            if (value.length() > MAX_TECH_STACK_ITEM_LENGTH) {
                throw new BusinessException("单项技术栈不能超过50个字符");
            }
            normalized.add(value);
        }
        if (normalized.size() > MAX_TECH_STACK_ITEMS) {
            throw new BusinessException("技术栈不能超过20项");
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

    /** 校验数据库使用的 0～4 项目状态码。 */
    public void validateStatus(Integer status) {
        if (!ProjectStatusConstant.isValid(status)) {
            throw new BusinessException("项目状态不合法");
        }
    }

    /** 精选标记默认关闭，并且只接受数据库约定的 0 或 1。 */
    public Integer normalizeFeatured(Integer featured) {
        int normalized = featured == null ? 0 : featured;
        if (normalized != 0 && normalized != 1) {
            throw new BusinessException("精选标记不合法");
        }
        return normalized;
    }

    /** 手动排序默认 0，并限制在简单、可管理的整数范围内。 */
    public Integer normalizeSortOrder(Integer sortOrder) {
        int normalized = sortOrder == null ? 0 : sortOrder;
        if (normalized < MIN_SORT_ORDER || normalized > MAX_SORT_ORDER) {
            throw new BusinessException("项目排序权重不合法");
        }
        return normalized;
    }

    /** 起止日期均可为空；同时存在时仅保证完成日期不早于开始日期。 */
    public void validateDates(LocalDate startedDate, LocalDate completedDate) {
        if (startedDate != null && completedDate != null && completedDate.isBefore(startedDate)) {
            throw new BusinessException("完成日期不能早于开始日期");
        }
    }

    private String normalizeRequired(String value, int maxLength, String requiredMessage, String lengthMessage) {
        if (value == null) {
            throw new BusinessException(requiredMessage);
        }
        String normalized = value.strip();
        if (normalized.isEmpty()) {
            throw new BusinessException(requiredMessage);
        }
        if (normalized.length() > maxLength) {
            throw new BusinessException(lengthMessage);
        }
        return normalized;
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
        if (normalized.length() > 500) {
            throw new BusinessException(errorMessage);
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
