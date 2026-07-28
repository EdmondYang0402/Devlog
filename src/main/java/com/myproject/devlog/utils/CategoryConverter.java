package com.myproject.devlog.utils;

import com.myproject.devlog.pojo.dto.CategoryCreateDTO;
import com.myproject.devlog.pojo.dto.CategoryUpdateDTO;
import com.myproject.devlog.pojo.entity.Category;
import com.myproject.devlog.pojo.vo.CategoryVO;

public final class CategoryConverter {
    private static final String NOTES_CATEGORY_NAME = "手记";
    private static final String NOTES_CATEGORY_SLUG = "notes";

    private CategoryConverter() {}

    public static Category toEntity(CategoryCreateDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setSortOrder(dto.getSortOrder() == null ? 0 : dto.getSortOrder());
        return category;
    }

    public static void updateEntity(Category category, CategoryUpdateDTO dto) {
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setSortOrder(dto.getSortOrder() == null ? 0 : dto.getSortOrder());
    }

    public static CategoryVO toVO(Category category, Long articleCount) {
        CategoryVO vo = new CategoryVO();
        vo.setId(category.getId());
        vo.setName(category.getName());
        // 现有表没有 slug 列；对受唯一名称约束的“手记”派生稳定标识，供前端使用而不依赖环境相关的 ID。
        vo.setSlug(NOTES_CATEGORY_NAME.equals(category.getName()) ? NOTES_CATEGORY_SLUG : null);
        vo.setDescription(category.getDescription());
        vo.setSortOrder(category.getSortOrder());
        vo.setArticleCount(articleCount);
        vo.setCreateTime(category.getCreateTime());
        vo.setUpdateTime(category.getUpdateTime());
        return vo;
    }
}
