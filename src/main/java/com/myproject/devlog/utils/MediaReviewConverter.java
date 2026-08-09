package com.myproject.devlog.utils;

import com.myproject.devlog.pojo.dto.MediaReviewCreateDTO;
import com.myproject.devlog.pojo.dto.MediaReviewUpdateDTO;
import com.myproject.devlog.pojo.entity.MediaReview;
import com.myproject.devlog.pojo.vo.MediaReviewDetailVO;
import com.myproject.devlog.pojo.vo.MediaReviewListVO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class MediaReviewConverter {
    private final MediaReviewValidator validator;

    public MediaReviewConverter(MediaReviewValidator validator) {
        this.validator = validator;
    }

    /** CreateDTO 先规范化字符串字段，再映射为待持久化实体。 */
    public MediaReview fromCreateDTO(MediaReviewCreateDTO dto) {
        validator.normalizeCreateDTO(dto);
        MediaReview entity = new MediaReview();
        copyEditableFields(entity, dto.getTitle(), dto.getMediaType(), dto.getStatus(), dto.getCoverUrl(),
                dto.getRating(), dto.getShortReview(), dto.getContent(), dto.getFinishedDate());
        return entity;
    }

    /** 更新只覆盖可编辑字段，原实体的 id 和 createTime 会被保留。 */
    public void applyUpdate(MediaReview existing, MediaReviewUpdateDTO dto) {
        validator.normalizeUpdateDTO(dto);
        copyEditableFields(existing, dto.getTitle(), dto.getMediaType(), dto.getStatus(), dto.getCoverUrl(),
                dto.getRating(), dto.getShortReview(), dto.getContent(), dto.getFinishedDate());
    }

    /** 列表 VO 不携带长评 content，减少分页接口的传输量。 */
    public MediaReviewListVO toListVO(MediaReview entity) {
        MediaReviewListVO vo = new MediaReviewListVO();
        vo.setId(entity.getId());
        vo.setTitle(entity.getTitle());
        vo.setMediaType(entity.getMediaType());
        vo.setStatus(entity.getStatus());
        vo.setCoverUrl(entity.getCoverUrl());
        vo.setRating(entity.getRating());
        vo.setShortReview(entity.getShortReview());
        vo.setFinishedDate(entity.getFinishedDate());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }

    /** 详情 VO 在列表字段基础上补充完整长评。 */
    public MediaReviewDetailVO toDetailVO(MediaReview entity) {
        MediaReviewDetailVO vo = new MediaReviewDetailVO();
        vo.setId(entity.getId());
        vo.setTitle(entity.getTitle());
        vo.setMediaType(entity.getMediaType());
        vo.setStatus(entity.getStatus());
        vo.setCoverUrl(entity.getCoverUrl());
        vo.setRating(entity.getRating());
        vo.setShortReview(entity.getShortReview());
        vo.setContent(entity.getContent());
        vo.setFinishedDate(entity.getFinishedDate());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }

    private void copyEditableFields(MediaReview entity, String title, Integer mediaType, Integer status,
                                    String coverUrl, Integer rating, String shortReview, String content,
                                    LocalDate finishedDate) {
        entity.setTitle(title);
        entity.setMediaType(mediaType);
        entity.setStatus(status);
        entity.setCoverUrl(coverUrl);
        entity.setRating(rating);
        entity.setShortReview(shortReview);
        entity.setContent(content);
        entity.setFinishedDate(finishedDate);
    }
}
