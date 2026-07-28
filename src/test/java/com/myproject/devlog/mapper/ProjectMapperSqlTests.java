package com.myproject.devlog.mapper;

import org.apache.ibatis.annotations.Select;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectMapperSqlTests {

    @Test
    void adminPageAndCountUseSameBoundFiltersAndExpectedOrdering() throws Exception {
        String pageSql = sql("adminPage");
        String countSql = sql("adminCount");

        for (String condition : new String[]{"#{keyword}", "#{status}", "#{featured}"}) {
            assertTrue(pageSql.contains(condition));
            assertTrue(countSql.contains(condition));
        }
        assertTrue(pageSql.contains("name LIKE CONCAT('%', #{keyword}, '%')"));
        assertTrue(pageSql.contains("summary LIKE CONCAT('%', #{keyword}, '%')"));
        assertTrue(pageSql.contains("ORDER BY sort_order DESC, update_time DESC, id DESC"));
        assertFalse(pageSql.contains("${"));
        assertFalse(countSql.contains("${"));
    }

    @Test
    void frontPageKeepsFeaturedWeightAndNullCompletionDateOrdering() throws Exception {
        String pageSql = sql("frontPage");
        String countSql = sql("frontCount");
        assertTrue(pageSql.contains("status = #{status}"));
        assertTrue(pageSql.contains("featured = #{featured}"));
        assertTrue(countSql.contains("status = #{status}"));
        assertTrue(countSql.contains("featured = #{featured}"));
        String compactSql = pageSql.replaceAll("\\s+", " ");
        assertTrue(compactSql.contains("ORDER BY featured DESC, sort_order DESC,"));
        assertTrue(pageSql.contains("CASE WHEN completed_date IS NULL THEN 1 ELSE 0 END"));
        assertTrue(pageSql.contains("completed_date DESC"));
        assertTrue(pageSql.contains("id DESC"));
    }

    @Test
    void featuredListFiltersAndLimitsInDatabase() throws Exception {
        String featuredSql = sql("featuredList");
        assertTrue(featuredSql.contains("WHERE featured = 1"));
        assertTrue(featuredSql.contains("ORDER BY sort_order DESC, id DESC"));
        assertTrue(featuredSql.contains("LIMIT #{limit}"));
    }

    private String sql(String methodName) throws Exception {
        Method method = Arrays.stream(ProjectMapper.class.getDeclaredMethods())
                .filter(candidate -> candidate.getName().equals(methodName))
                .findFirst()
                .orElseThrow();
        return String.join(" ", method.getAnnotation(Select.class).value());
    }
}
