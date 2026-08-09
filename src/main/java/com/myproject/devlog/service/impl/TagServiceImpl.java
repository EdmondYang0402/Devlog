package com.myproject.devlog.service.impl;

import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.mapper.TagMapper;
import com.myproject.devlog.pojo.dto.TagCreateDTO;
import com.myproject.devlog.pojo.dto.TagUpdateDTO;
import com.myproject.devlog.pojo.entity.Tag;
import com.myproject.devlog.pojo.vo.AdminTagVO;
import com.myproject.devlog.pojo.vo.TagVO;
import com.myproject.devlog.service.TagService;
import com.myproject.devlog.service.CategoryTagService;
import com.myproject.devlog.utils.TagConverter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    private final TagMapper tagMapper;
    private final CategoryTagService categoryTagService;

    public TagServiceImpl(TagMapper tagMapper, CategoryTagService categoryTagService) {
        this.tagMapper = tagMapper;
        this.categoryTagService = categoryTagService;
    }

    @Override
    public void create(TagCreateDTO dto) {
        String name = dto.getName().trim();

        if (tagMapper.existsByName(name)) {
            throw new BusinessException(HttpStatus.CONFLICT, "标签已存在");
        }

        Tag tag = TagConverter.fromCreateDTO(dto);
        tag.setName(name);

        if (tagMapper.insert(tag) != 1) {
            throw new IllegalStateException("标签创建未影响预期记录数");
        }
    }

    @Override
    public void update(Long id, TagUpdateDTO dto) {
        Tag tag = tagMapper.selectById(id);

        if (tag == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "标签不存在");
        }

        String name = dto.getName().trim();

        if (tagMapper.existsByNameExcludeId(name, id)) {
            throw new BusinessException(HttpStatus.CONFLICT, "标签名称已存在");
        }

        tag.setName(name);
        if (tagMapper.updateById(tag) != 1) {
            throw new IllegalStateException("标签更新未影响预期记录数");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Tag tag = tagMapper.selectById(id);
        if (tag == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "标签不存在");
        }
        if (tagMapper.countArticleReferences(id) > 0) {
            throw new BusinessException(HttpStatus.CONFLICT, "该标签仍被文章引用，无法删除");
        }
        // article_tag 的现有引用保护保持不变；仅清理本次新增的 category_tag 关系。
        categoryTagService.deleteByTagId(id);
        if (tagMapper.deleteById(id) == 0) {
            throw new IllegalStateException("标签删除未影响任何记录");
        }
    }

    @Override
    public List<Tag> getByCategoryId(Long categoryId) {
        return tagMapper.selectByCategoryId(categoryId);
    }

    @Override
    public List<TagVO> list() {
        return tagMapper.selectList()
                .stream()
                .map(TagConverter::toVO)
                .toList();
    }

    @Override
    public List<AdminTagVO> listAdmin() {
        return tagMapper.selectAdminListWithArticleCount();
    }
}
