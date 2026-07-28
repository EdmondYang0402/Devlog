package com.myproject.devlog.utils;

import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.common.MediaStatusConstant;
import com.myproject.devlog.common.MediaTypeConstant;
import com.myproject.devlog.pojo.dto.MediaReviewCreateDTO;
import com.myproject.devlog.pojo.dto.MediaReviewUpdateDTO;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;

import static com.myproject.devlog.utils.UploadUrlUtil.isLocalUploadUrl;

@Component
public class MediaReviewValidator {

    /** 规范化作品标题，并保证数据库必填字段满足长度限制。 */
    public String normalizeTitle(String title) {
        if (title == null) {
            throw new BusinessException("标题不能为空");
        }
        String normalized = title.strip();
        if (normalized.isEmpty()) {
            throw new BusinessException("标题不能为空");
        }
        if (normalized.length() > 200) {
            throw new BusinessException("标题不能超过200个字符");
        }
        return normalized;
    }

    /** 校验统一作品类型码。 */
    public void validateMediaType(Integer mediaType) {
        if (!MediaTypeConstant.isValid(mediaType)) {
            throw new BusinessException("作品类型不合法");
        }
    }

    /** 校验跨作品类型共用的状态码。 */
    public void validateStatus(Integer status) {
        if (!MediaStatusConstant.isValid(status)) {
            throw new BusinessException("作品状态不合法");
        }
    }

    /** 校验 1～10 分制评分；其中 1 分对应前端半星，null 表示未评分。 */
    public Integer validateRating(Integer rating) {
        if (rating != null && (rating < 1 || rating > 10)) {
            throw new BusinessException("评分必须在1到10分之间");
        }
        return rating;
    }

    /** 规范化封面地址；这里只验证 URI 格式，不会访问或下载远程图片。 */
    public String normalizeCoverUrl(String coverUrl) {
        if (coverUrl == null) {
            return null;
        }
        String normalized = coverUrl.strip();
        if (normalized.isEmpty()) {
            return null;
        }
        if (normalized.length() > 500) {
            throw new BusinessException("封面地址不能超过500个字符");
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
                throw new BusinessException("封面地址必须是HTTP或HTTPS链接");
            }
        } catch (URISyntaxException exception) {
            throw new BusinessException("封面地址必须是HTTP或HTTPS链接");
        }
        return normalized;
    }

    /** 规范化列表短评，空白内容按未填写处理。 */
    public String normalizeShortReview(String shortReview) {
        if (shortReview == null) {
            return null;
        }
        String normalized = shortReview.strip();
        if (normalized.isEmpty()) {
            return null;
        }
        if (normalized.length() > 500) {
            throw new BusinessException("短评不能超过500个字符");
        }
        return normalized;
    }

    /** 规范化详细评价；内容展示方式由前端负责。 */
    public String normalizeContent(String content) {
        if (content == null) {
            return null;
        }
        String normalized = content.strip();
        return normalized.isEmpty() ? null : normalized;
    }

    /** 完成日期允许为空，也不与状态强绑定，便于补录只有大致时间的历史作品。 */
    public LocalDate validateFinishedDate(LocalDate finishedDate) {
        return finishedDate;
    }

    /** 对新增请求执行一次完整校验和规范化。 */
    public void validateCreateDTO(MediaReviewCreateDTO dto) {
        if (dto == null) {
            throw new BusinessException("作品信息不能为空");
        }
        dto.setTitle(normalizeTitle(dto.getTitle()));
        validateMediaType(dto.getMediaType());
        validateStatus(dto.getStatus());
        dto.setRating(validateRating(dto.getRating()));
        dto.setCoverUrl(normalizeCoverUrl(dto.getCoverUrl()));
        dto.setShortReview(normalizeShortReview(dto.getShortReview()));
        dto.setContent(normalizeContent(dto.getContent()));
        dto.setFinishedDate(validateFinishedDate(dto.getFinishedDate()));
    }

    /** PUT 使用全量更新语义，因此更新请求沿用与新增相同的字段规则。 */
    public void validateUpdateDTO(MediaReviewUpdateDTO dto) {
        if (dto == null) {
            throw new BusinessException("作品信息不能为空");
        }
        dto.setTitle(normalizeTitle(dto.getTitle()));
        validateMediaType(dto.getMediaType());
        validateStatus(dto.getStatus());
        dto.setRating(validateRating(dto.getRating()));
        dto.setCoverUrl(normalizeCoverUrl(dto.getCoverUrl()));
        dto.setShortReview(normalizeShortReview(dto.getShortReview()));
        dto.setContent(normalizeContent(dto.getContent()));
        dto.setFinishedDate(validateFinishedDate(dto.getFinishedDate()));
    }
}
