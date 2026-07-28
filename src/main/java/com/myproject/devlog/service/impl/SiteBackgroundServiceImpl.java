package com.myproject.devlog.service.impl;

import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.common.PageResult;
import com.myproject.devlog.mapper.SiteBackgroundMapper;
import com.myproject.devlog.pojo.dto.SiteBackgroundCreateDTO;
import com.myproject.devlog.pojo.dto.SiteBackgroundPageQueryDTO;
import com.myproject.devlog.pojo.dto.SiteBackgroundUpdateDTO;
import com.myproject.devlog.pojo.entity.SiteBackground;
import com.myproject.devlog.pojo.vo.SiteBackgroundAdminVO;
import com.myproject.devlog.pojo.vo.SiteBackgroundPublicVO;
import com.myproject.devlog.service.SiteBackgroundService;
import com.myproject.devlog.utils.SiteBackgroundConverter;
import com.myproject.devlog.utils.SiteBackgroundValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SiteBackgroundServiceImpl implements SiteBackgroundService {
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;

    private final SiteBackgroundMapper mapper;
    private final SiteBackgroundConverter converter;
    private final SiteBackgroundValidator validator;

    public SiteBackgroundServiceImpl(SiteBackgroundMapper mapper,
                                     SiteBackgroundConverter converter,
                                     SiteBackgroundValidator validator) {
        this.mapper = mapper;
        this.converter = converter;
        this.validator = validator;
    }

    /** 规范化请求后写库，并同时检查影响行数和数据库生成的主键。 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(SiteBackgroundCreateDTO dto) {
        if (dto == null) {
            throw new BusinessException("背景图片信息不能为空");
        }
        SiteBackground entity = converter.fromCreateDTO(dto);
        if (mapper.insert(entity) != 1 || entity.getId() == null) {
            throw new BusinessException("背景图片记录创建失败");
        }
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, SiteBackgroundUpdateDTO dto) {
        if (dto == null) {
            throw new BusinessException("背景图片信息不能为空");
        }
        SiteBackground existing = getRequiredBackground(id);
        converter.applyUpdate(existing, dto);
        if (mapper.update(existing) != 1) {
            throw new BusinessException("背景图片记录更新失败");
        }
    }

    /** 仅删除数据库记录，不删除可能被其他内容复用的图片，避免产生失效链接。 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        getRequiredBackground(id);
        if (mapper.deleteById(id) != 1) {
            throw new BusinessException("背景图片记录删除失败");
        }
    }

    @Override
    public SiteBackgroundAdminVO getAdminDetail(Long id) {
        return converter.toAdminVO(getRequiredBackground(id));
    }

    /** 筛选和分页全部交给数据库，避免配置记录增长后出现内存分页。 */
    @Override
    public PageResult<SiteBackgroundAdminVO> adminPage(SiteBackgroundPageQueryDTO query) {
        SiteBackgroundPageQueryDTO normalized = query == null ? new SiteBackgroundPageQueryDTO() : query;
        int page = normalizePage(normalized.getPage());
        int size = normalizeSize(normalized.getSize());
        String keyword = normalizeKeyword(normalized.getKeyword());
        Integer enabled = normalized.getEnabled();
        if (enabled != null) {
            validator.normalizeEnabled(enabled);
        }
        int offset = calculateOffset(page, size);
        List<SiteBackgroundAdminVO> records = mapper.adminPage(offset, size, keyword, enabled)
                .stream().map(converter::toAdminVO).toList();
        return new PageResult<>(records, mapper.adminCount(keyword, enabled));
    }

    /** V1 直接读取少量启用记录，不引入 Redis 及随之而来的缓存失效逻辑。 */
    @Override
    public List<SiteBackgroundPublicVO> getEnabledBackgrounds() {
        List<SiteBackground> records = mapper.selectEnabledList();
        if (records == null || records.isEmpty()) {
            return List.of();
        }
        return records.stream().map(converter::toPublicVO).toList();
    }

    /** 集中提供详情、更新和删除共用的 404 语义。 */
    private SiteBackground getRequiredBackground(Long id) {
        if (id == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "背景图片记录不存在");
        }
        SiteBackground entity = mapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "背景图片记录不存在");
        }
        return entity;
    }

    private int normalizePage(Integer page) {
        int normalized = page == null ? 1 : page;
        if (normalized < 1) {
            throw new BusinessException("分页参数不合法");
        }
        return normalized;
    }

    private int normalizeSize(Integer size) {
        int normalized = size == null ? DEFAULT_PAGE_SIZE : size;
        if (normalized < 1 || normalized > MAX_PAGE_SIZE) {
            throw new BusinessException("分页参数不合法");
        }
        return normalized;
    }

    private int calculateOffset(int page, int size) {
        long offset = (long) (page - 1) * size;
        if (offset > Integer.MAX_VALUE) {
            throw new BusinessException("分页参数不合法");
        }
        return (int) offset;
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        String normalized = keyword.strip();
        if (normalized.length() > 100) {
            throw new BusinessException("搜索关键词不能超过100个字符");
        }
        return normalized;
    }
}
