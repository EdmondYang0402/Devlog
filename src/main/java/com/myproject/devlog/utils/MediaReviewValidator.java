package com.myproject.devlog.utils;

import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.common.MediaStatusConstant;
import com.myproject.devlog.common.MediaTypeConstant;
import com.myproject.devlog.pojo.dto.MediaReviewCreateDTO;
import com.myproject.devlog.pojo.dto.MediaReviewUpdateDTO;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

import static com.myproject.devlog.utils.UploadUrlUtil.isLocalUploadUrl;

@Component
public class MediaReviewValidator {

    /** 必填与长度由 DTO 保证，这里只统一去除首尾空白。 */
    public String normalizeTitle(String title) {
        return title.strip();
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

    /** 规范化封面地址；这里只验证 URI 格式，不会访问或下载远程图片。 */
    public String normalizeCoverUrl(String coverUrl) {
        if (coverUrl == null) {
            return null;
        }
        String normalized = coverUrl.strip();
        if (normalized.isEmpty()) {
            return null;
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

    /** DTO 已完成单字段结构校验；这里仅执行写库前规范化。 */
    public void normalizeCreateDTO(MediaReviewCreateDTO dto) {
        dto.setTitle(normalizeTitle(dto.getTitle()));
        dto.setCoverUrl(normalizeCoverUrl(dto.getCoverUrl()));
        dto.setShortReview(normalizeShortReview(dto.getShortReview()));
        dto.setContent(normalizeContent(dto.getContent()));
    }

    /** PUT 使用全量更新语义，规范化规则与新增一致。 */
    public void normalizeUpdateDTO(MediaReviewUpdateDTO dto) {
        dto.setTitle(normalizeTitle(dto.getTitle()));
        dto.setCoverUrl(normalizeCoverUrl(dto.getCoverUrl()));
        dto.setShortReview(normalizeShortReview(dto.getShortReview()));
        dto.setContent(normalizeContent(dto.getContent()));
    }
}
