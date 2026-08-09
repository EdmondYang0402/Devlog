package com.myproject.devlog.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryTagMapperSqlTests {
    @Test
    void schemaUsesCompositePrimaryKeyAndReverseIndexWithoutForeignKeys() throws Exception {
        String sql = Files.readString(Path.of("database/category_tag.sql"));

        assertTrue(sql.contains("PRIMARY KEY (category_id, tag_id)"));
        assertTrue(sql.contains("KEY idx_category_tag_tag_id (tag_id)"));
        assertFalse(sql.toUpperCase().contains("FOREIGN KEY"));
        assertFalse(sql.matches("(?s).*\bid\s+BIGINT\s+NOT NULL\s+AUTO_INCREMENT.*"));
    }

    @Test
    void categoryAndArticleRelationsRemainSeparateTables() throws Exception {
        String categoryInsert = sql(CategoryTagMapper.class, "insertBatch", Insert.class);
        String articleInsert = sql(ArticleTagMapper.class, "insertBatch", Insert.class);

        assertTrue(categoryInsert.contains("INSERT INTO category_tag"));
        assertFalse(categoryInsert.contains("article_tag"));
        assertTrue(articleInsert.contains("INSERT INTO article_tag"));
        assertFalse(articleInsert.contains("category_tag"));
    }

    @Test
    void mapperUsesBulkQueriesAndBulkInsert() throws Exception {
        String insert = sql(CategoryTagMapper.class, "insertBatch", Insert.class);
        String selectTags = sql(CategoryTagMapper.class, "selectTagsByCategoryId", Select.class);
        String selectByIds = sql(TagMapper.class, "selectByIds", Select.class);

        assertTrue(insert.contains("<foreach"));
        assertTrue(selectTags.contains("JOIN tag"));
        assertTrue(selectByIds.contains("WHERE id IN"));
        assertTrue(selectByIds.contains("<foreach"));
    }

    @Test
    void mapperSupportsBothCleanupDirectionsAndSingleRelationDeletion() throws Exception {
        String deleteCategory = sql(CategoryTagMapper.class, "deleteByCategoryId", Delete.class);
        String deleteTag = sql(CategoryTagMapper.class, "deleteByTagId", Delete.class);
        String deleteOne = sql(CategoryTagMapper.class,
                "deleteByCategoryIdAndTagId", Delete.class);

        assertTrue(deleteCategory.contains("category_id = #{categoryId}"));
        assertTrue(deleteTag.contains("tag_id = #{tagId}"));
        assertTrue(deleteOne.contains("category_id = #{categoryId}"));
        assertTrue(deleteOne.contains("tag_id = #{tagId}"));
    }

    private <A extends java.lang.annotation.Annotation> String sql(
            Class<?> mapperType, String methodName, Class<A> annotationType) throws Exception {
        var method = java.util.Arrays.stream(mapperType.getDeclaredMethods())
                .filter(candidate -> candidate.getName().equals(methodName))
                .findFirst()
                .orElseThrow();
        A annotation = method.getAnnotation(annotationType);
        if (annotation instanceof Select select) {
            return String.join(" ", select.value());
        }
        if (annotation instanceof Insert insert) {
            return String.join(" ", insert.value());
        }
        if (annotation instanceof Delete delete) {
            return String.join(" ", delete.value());
        }
        throw new IllegalArgumentException("不支持的 SQL 注解");
    }
}
