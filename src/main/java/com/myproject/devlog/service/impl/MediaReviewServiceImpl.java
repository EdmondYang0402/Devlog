package com.myproject.devlog.service.impl;

import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.common.PageResult;
import com.myproject.devlog.mapper.MediaReviewMapper;
import com.myproject.devlog.pojo.dto.MediaReviewCreateDTO;
import com.myproject.devlog.pojo.dto.MediaReviewUpdateDTO;
import com.myproject.devlog.pojo.entity.MediaReview;
import com.myproject.devlog.pojo.vo.MediaReviewDetailVO;
import com.myproject.devlog.pojo.vo.MediaReviewListVO;
import com.myproject.devlog.service.MediaReviewService;
import com.myproject.devlog.utils.MediaReviewConverter;
import com.myproject.devlog.utils.MediaReviewValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MediaReviewServiceImpl implements MediaReviewService {
    private static final int MAX_PAGE_SIZE = 100;

    private final MediaReviewMapper mediaReviewMapper;
    private final MediaReviewValidator mediaReviewValidator;
    private final MediaReviewConverter mediaReviewConverter;

    public MediaReviewServiceImpl(MediaReviewMapper mediaReviewMapper,
                                  MediaReviewValidator mediaReviewValidator,
                                  MediaReviewConverter mediaReviewConverter) {
        this.mediaReviewMapper = mediaReviewMapper;
        this.mediaReviewValidator = mediaReviewValidator;
        this.mediaReviewConverter = mediaReviewConverter;
    }

    /** 新增流程：规范化 DTO、转换实体、写库并核对影响行数。 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(MediaReviewCreateDTO dto) {
        MediaReview entity = mediaReviewConverter.fromCreateDTO(dto);
        if (mediaReviewMapper.insert(entity) != 1) {
            throw new IllegalStateException("作品记录创建未影响预期记录数");
        }
    }

    /** 更新流程：先取得原实体，再以 PUT 全量语义覆盖可编辑字段。 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, MediaReviewUpdateDTO dto) {
        MediaReview existing = getRequiredReview(id);
        mediaReviewConverter.applyUpdate(existing, dto);
        if (mediaReviewMapper.updateById(existing) != 1) {
            throw new IllegalStateException("作品记录更新未影响预期记录数");
        }
    }

    /** 删除前先确认资源存在，避免把重复删除误报为成功。 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        getRequiredReview(id);
        if (mediaReviewMapper.deleteById(id) != 1) {
            throw new IllegalStateException("作品记录删除未影响预期记录数");
        }
    }

    @Override
    public MediaReviewDetailVO getAdminById(Long id) {
        return mediaReviewConverter.toDetailVO(getRequiredReview(id));
    }

    /** 后台分页保留全部状态，并让数据库同时完成筛选、排序和分页。 */
    @Override
    public PageResult<MediaReviewListVO> pageAdmin(Integer page, Integer size, String title,
                                                    Integer mediaType, Integer status) {
        validatePage(page, size);
        String normalizedTitle = normalizeSearchTitle(title);
        if (mediaType != null) {
            mediaReviewValidator.validateMediaType(mediaType);
        }
        if (status != null) {
            mediaReviewValidator.validateStatus(status);
        }

        int offset = (page - 1) * size;
        List<MediaReviewListVO> records = mediaReviewMapper
                .selectAdminPage(offset, size, normalizedTitle, mediaType, status)
                .stream()
                .map(mediaReviewConverter::toListVO)
                .toList();
        long total = mediaReviewMapper.countAdmin(normalizedTitle, mediaType, status);
        return new PageResult<>(records, total);
    }

    @Override
    public MediaReviewDetailVO getById(Long id) {
        // 现有前台有“进行中 / 计划中”区域，因此详情允许展示所有状态，而不是只公开已完成记录。
        return mediaReviewConverter.toDetailVO(getRequiredReview(id));
    }

    /** 前台时间轴与矩阵共用相同分页结果，视图切换不会改变后端查询契约。 */
    @Override
    public PageResult<MediaReviewListVO> page(Integer page, Integer size,
                                                       Integer mediaType, String sort) {
        validatePage(page, size);
        if (mediaType != null) {
            mediaReviewValidator.validateMediaType(mediaType);
        }
        String normalizedSort = normalizeSort(sort);
        int offset = (page - 1) * size;
        List<MediaReviewListVO> records = mediaReviewMapper
                .selectFrontPage(offset, size, mediaType, normalizedSort)
                .stream()
                .map(mediaReviewConverter::toListVO)
                .toList();
        long total = mediaReviewMapper.countFront(mediaType);
        return new PageResult<>(records, total);
    }

    /** 集中处理不存在的作品记录，让查询、更新和删除保持同一套 404 语义。 */
    private MediaReview getRequiredReview(Long id) {
        if (id == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "作品记录不存在");
        }
        MediaReview entity = mediaReviewMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "作品记录不存在");
        }
        return entity;
    }

    private void validatePage(Integer page, Integer size) {
        if (page == null || page < 1 || size == null || size < 1 || size > MAX_PAGE_SIZE
                || (long) (page - 1) * size > Integer.MAX_VALUE) {
            throw new BusinessException("分页参数不合法");
        }
    }

    private String normalizeSearchTitle(String title) {
        if (title == null) {
            return null;
        }
        String normalized = title.strip();
        if (normalized.isEmpty()) {
            return null;
        }
        if (normalized.length() > 200) {
            throw new BusinessException("搜索标题不能超过200个字符");
        }
        return normalized;
    }

    private String normalizeSort(String sort) {
        String normalized = sort == null || sort.isBlank() ? "latest" : sort.strip();
        if (!"latest".equals(normalized) && !"rating".equals(normalized)) {
            throw new BusinessException("排序方式不合法");
        }
        return normalized;
    }
}
