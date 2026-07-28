package com.myproject.devlog.mapper;

import org.apache.ibatis.annotations.Select;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SiteBackgroundMapperSqlTests {

    @Test
    void adminPageAndCountUseSameBoundFiltersAndDatabasePagination() throws Exception {
        String pageSql = sql("adminPage");
        String countSql = sql("adminCount");
        assertTrue(pageSql.contains("title LIKE CONCAT('%', #{keyword}, '%')"));
        assertTrue(countSql.contains("title LIKE CONCAT('%', #{keyword}, '%')"));
        assertTrue(pageSql.contains("enabled = #{enabled}"));
        assertTrue(countSql.contains("enabled = #{enabled}"));
        assertTrue(pageSql.contains("ORDER BY sort_order DESC, update_time DESC, id DESC"));
        assertTrue(pageSql.contains("LIMIT #{offset}, #{size}"));
        assertFalse(pageSql.contains("${"));
        assertFalse(countSql.contains("${"));
    }

    @Test
    void publicQueryFiltersEnabledRowsAndSortsByWeightInDatabase() throws Exception {
        String sql = sql("selectEnabledList");
        assertTrue(sql.contains("WHERE enabled = 1"));
        assertTrue(sql.contains("ORDER BY sort_order DESC, id DESC"));
        assertFalse(sql.contains("enabled = #{enabled}"));
    }

    private String sql(String methodName) throws Exception {
        Method method = Arrays.stream(SiteBackgroundMapper.class.getDeclaredMethods())
                .filter(candidate -> candidate.getName().equals(methodName))
                .findFirst().orElseThrow();
        return String.join(" ", method.getAnnotation(Select.class).value());
    }
}
