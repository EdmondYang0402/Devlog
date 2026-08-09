package com.myproject.devlog.service.impl;

import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.mapper.CategoryMapper;
import com.myproject.devlog.mapper.CategoryTagMapper;
import com.myproject.devlog.mapper.TagMapper;
import com.myproject.devlog.pojo.entity.CategoryTag;
import com.myproject.devlog.pojo.entity.Tag;
import com.myproject.devlog.pojo.vo.TagVO;
import com.myproject.devlog.service.CategoryTagService;
import com.myproject.devlog.utils.TagConverter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class CategoryTagServiceImpl implements CategoryTagService {
    private final CategoryTagMapper categoryTagMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;

    public CategoryTagServiceImpl(CategoryTagMapper categoryTagMapper,
                                  CategoryMapper categoryMapper,
                                  TagMapper tagMapper) {
        this.categoryTagMapper = categoryTagMapper;
        this.categoryMapper = categoryMapper;
        this.tagMapper = tagMapper;
    }

    @Override
    public List<TagVO> listTagsByCategoryId(Long categoryId) {
        requireCategory(categoryId);
        return categoryTagMapper.selectTagsByCategoryId(categoryId)
                .stream()
                .map(TagConverter::toVO)
                .toList();
    }

    /**
     * 整体替换必须处于同一事务，避免旧关系已删除而新关系只写入一部分。
     * category_tag 表达知识分类，article_tag 仍由文章标签服务独立维护。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setTagsForCategory(Long categoryId, List<Long> tagIds) {
        requireCategory(categoryId);

        List<Long> distinctTagIds = new ArrayList<>(new LinkedHashSet<>(tagIds));
        if (distinctTagIds.isEmpty()) {
            categoryTagMapper.deleteByCategoryId(categoryId);
            return;
        }

        // 一次批量查询完成存在性校验，避免按 tagId 循环查询造成 N+1。
        List<Tag> existingTags = tagMapper.selectByIds(distinctTagIds);
        Set<Long> existingTagIds = existingTags.stream()
                .map(Tag::getId)
                .collect(java.util.stream.Collectors.toSet());
        Long missingTagId = distinctTagIds.stream()
                .filter(tagId -> !existingTagIds.contains(tagId))
                .findFirst()
                .orElse(null);
        if (missingTagId != null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "标签不存在：" + missingTagId);
        }

        categoryTagMapper.deleteByCategoryId(categoryId);
        List<CategoryTag> relations = distinctTagIds.stream()
                .map(tagId -> relation(categoryId, tagId))
                .toList();
        if (categoryTagMapper.insertBatch(relations) != relations.size()) {
            throw new IllegalStateException("分类标签关系写入未影响预期记录数");
        }
    }

    @Override
    public void deleteByCategoryId(Long categoryId) {
        categoryTagMapper.deleteByCategoryId(categoryId);
    }

    @Override
    public void deleteByTagId(Long tagId) {
        categoryTagMapper.deleteByTagId(tagId);
    }

    private void requireCategory(Long categoryId) {
        if (categoryMapper.selectById(categoryId) == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "分类不存在");
        }
    }

    private CategoryTag relation(Long categoryId, Long tagId) {
        CategoryTag relation = new CategoryTag();
        relation.setCategoryId(categoryId);
        relation.setTagId(tagId);
        return relation;
    }
}
