package com.myproject.devlog.service;

import com.myproject.devlog.common.PageResult;
import com.myproject.devlog.mapper.ArticleMapper;
import com.myproject.devlog.pojo.vo.ArticleListVO;
import com.myproject.devlog.service.impl.ArticleServiceImpl;
import org.apache.ibatis.annotations.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleTagFilterTests {
    @Mock
    private ArticleMapper articleMapper;
    @Mock
    private ArticleTagService articleTagService;
    private ArticleServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ArticleServiceImpl(articleMapper, articleTagService);
    }

    @Test
    void noTagIdsUsesOrdinaryPublishedPageAndKeepsUntaggedArticles() {
        ArticleListVO article = article(1L);
        when(articleMapper.selectFrontPage(0, 10, null, null, null)).thenReturn(List.of(article));
        when(articleMapper.countFront(null, null, null)).thenReturn(1);
        when(articleTagService.listByArticleIds(List.of(1L))).thenReturn(Map.of());

        PageResult<ArticleListVO> result = service.page(1, 10, null, null, null, null);

        assertEquals(1, result.getTotal());
        assertEquals(List.of(), result.getRecords().getFirst().getTags());
        verify(articleMapper, never()).selectFrontPageByTagIds(
                anyInt(), anyInt(), any(), any(), any(), anyList(), anyInt()
        );
    }

    @Test
    void emptyAndNullOnlyTagIdsUseOrdinaryQuery() {
        when(articleMapper.selectFrontPage(0, 10, null, null, null)).thenReturn(List.of());
        when(articleMapper.countFront(null, null, null)).thenReturn(0);

        service.page(1, 10, null, null, "  ", List.of());
        service.page(1, 10, null, null, null, java.util.Arrays.asList(null, null));

        verify(articleMapper, times(2)).selectFrontPage(0, 10, null, null, null);
        verify(articleMapper, never()).selectFrontPageByTagIds(
                anyInt(), anyInt(), any(), any(), any(), anyList(), anyInt()
        );
    }

    @Test
    void duplicateAndNullTagIdsAreCleanedBeforeAndFilterQuery() {
        List<Long> cleaned = List.of(3L, 5L);
        when(articleMapper.selectFrontPageByTagIds(10, 10, 2L, null, "JWT", cleaned, 2))
                .thenReturn(List.of(article(8L)));
        when(articleMapper.countFrontByTagIds(2L, null, "JWT", cleaned, 2)).thenReturn(1);
        when(articleTagService.listByArticleIds(List.of(8L))).thenReturn(Map.of());

        PageResult<ArticleListVO> result = service.page(
                2, 10, 2L, null, " JWT ", java.util.Arrays.asList(3L, null, 3L, 5L)
        );

        assertEquals(1, result.getTotal());
        verify(articleMapper).selectFrontPageByTagIds(10, 10, 2L, null, "JWT", cleaned, 2);
        verify(articleMapper).countFrontByTagIds(2L, null, "JWT", cleaned, 2);
        verify(articleMapper, never()).selectFrontPage(anyInt(), anyInt(), any(), any(), any());
    }

    @Test
    void nonexistentTagNaturallyReturnsEmptyFilteredPage() {
        when(articleMapper.selectFrontPageByTagIds(0, 10, null, null, null, List.of(999L), 1))
                .thenReturn(List.of());
        when(articleMapper.countFrontByTagIds(null, null, null, List.of(999L), 1)).thenReturn(0);

        PageResult<ArticleListVO> result = service.page(
                1, 10, null, null, null, List.of(999L)
        );

        assertTrue(result.getRecords().isEmpty());
        assertEquals(0, result.getTotal());
    }

    @Test
    void notesSlugUsesStableUniqueCategoryNameInBothPageAndCount() {
        when(articleMapper.selectFrontPage(0, 10, null, "手记", null)).thenReturn(List.of());
        when(articleMapper.countFront(null, "手记", null)).thenReturn(0);

        service.page(1, 10, null, " notes ", null, null);

        verify(articleMapper).selectFrontPage(0, 10, null, "手记", null);
        verify(articleMapper).countFront(null, "手记", null);
    }

    @Test
    void mapperSqlEnforcesPublishedAndSemanticsAndCorrectCount() throws Exception {
        String pageSql = sql("selectFrontPageByTagIds");
        String countSql = sql("countFrontByTagIds");
        String ordinarySql = sql("selectFrontPage");
        String ordinaryCountSql = sql("countFront");

        assertTrue(pageSql.matches("(?s).*a\\.status\\s*=\\s*1.*"));
        assertTrue(pageSql.contains("at.tag_id IN"));
        assertTrue(pageSql.matches("(?s).*GROUP BY\\s+at\\.article_id.*"));
        assertTrue(pageSql.matches("(?s).*HAVING COUNT\\(DISTINCT at\\.tag_id\\)\\s*=\\s*#\\{tagCount}.*"));
        assertTrue(pageSql.contains("a.category_id = #{categoryId}"));
        assertTrue(pageSql.contains("c.name = #{categoryName}"));
        assertTrue(pageSql.contains("a.title LIKE") && pageSql.contains("a.summary LIKE"));
        assertTrue(pageSql.contains("ORDER BY a.create_time DESC"));
        assertTrue(countSql.contains("COUNT(DISTINCT a.id)"));
        assertTrue(countSql.contains("c.name = #{categoryName}"));
        assertTrue(countSql.contains("HAVING COUNT(DISTINCT at.tag_id) = #{tagCount}"));
        assertTrue(ordinarySql.contains("c.name = #{categoryName}"));
        assertTrue(ordinaryCountSql.contains("c.name = #{categoryName}"));
        assertTrue(ordinaryCountSql.matches("(?s).*a\\.status\\s*=\\s*1.*"));
        assertFalse(ordinarySql.contains("JOIN article_tag"));
    }

    private String sql(String methodName) throws Exception {
        Method method = java.util.Arrays.stream(ArticleMapper.class.getDeclaredMethods())
                .filter(candidate -> candidate.getName().equals(methodName))
                .findFirst().orElseThrow();
        return String.join(" ", method.getAnnotation(Select.class).value());
    }

    private ArticleListVO article(Long id) {
        ArticleListVO article = new ArticleListVO();
        article.setId(id);
        article.setTitle("Article " + id);
        return article;
    }
}
