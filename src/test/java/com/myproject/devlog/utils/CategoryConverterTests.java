package com.myproject.devlog.utils;

import com.myproject.devlog.pojo.entity.Category;
import com.myproject.devlog.pojo.vo.CategoryVO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CategoryConverterTests {

    @Test
    void notesCategoryExposesStableSlugWithoutDependingOnId() {
        Category category = new Category();
        category.setId(37L);
        category.setName("手记");

        CategoryVO result = CategoryConverter.toVO(category, 0L);

        assertEquals("notes", result.getSlug());
    }

    @Test
    void ordinaryCategoryDoesNotInventSlug() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Java");

        CategoryVO result = CategoryConverter.toVO(category, 0L);

        assertNull(result.getSlug());
    }
}
