package com.myproject.devlog.service;

import com.myproject.devlog.mapper.ArticleTagMapper;
import com.myproject.devlog.mapper.TagMapper;
import com.myproject.devlog.pojo.vo.ArticleTagQueryVO;
import com.myproject.devlog.pojo.vo.TagVO;
import com.myproject.devlog.service.impl.ArticleTagServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleTagServiceImplTests {
    @Mock
    private ArticleTagMapper articleTagMapper;
    @Mock
    private TagMapper tagMapper;
    private ArticleTagServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ArticleTagServiceImpl(articleTagMapper, tagMapper);
    }

    @Test
    void batchListUsesOneMapperCallAndGroupsByArticleId() {
        when(articleTagMapper.listTagsByArticleIds(List.of(10L, 20L)))
                .thenReturn(List.of(row(10L, 1L, "Java"), row(10L, 2L, "JWT")));

        Map<Long, List<TagVO>> result = service.listByArticleIds(List.of(10L, 20L, 10L));

        assertEquals(List.of("Java", "JWT"), result.get(10L).stream().map(TagVO::getName).toList());
        assertFalse(result.containsKey(20L));
        verify(articleTagMapper, times(1)).listTagsByArticleIds(List.of(10L, 20L));
        verify(articleTagMapper, never()).listTagsByArticleId(anyLong());
    }

    @Test
    void emptyBatchDoesNotQueryMapper() {
        assertTrue(service.listByArticleIds(List.of()).isEmpty());
        verifyNoInteractions(articleTagMapper, tagMapper);
    }

    @Test
    void emptyMapperResultReturnsEmptyMap() {
        when(articleTagMapper.listTagsByArticleIds(List.of(10L))).thenReturn(List.of());
        assertTrue(service.listByArticleIds(List.of(10L)).isEmpty());
    }

    @Test
    void deleteByArticleIdUsesExplicitCleanupForNoForeignKeySchema() {
        service.deleteByArticleId(10L);
        verify(articleTagMapper).deleteByArticleId(10L);
    }

    @Test
    void userOwnedSingleAndReplaceMethodsRemainExplicitTodos() {
        assertThrows(UnsupportedOperationException.class, () -> service.listByArticleId(1L));
        assertThrows(UnsupportedOperationException.class, () -> service.replaceArticleTags(1L, List.of(2L)));
        verifyNoInteractions(articleTagMapper, tagMapper);
    }

    private ArticleTagQueryVO row(Long articleId, Long tagId, String name) {
        ArticleTagQueryVO row = new ArticleTagQueryVO();
        row.setArticleId(articleId);
        row.setTagId(tagId);
        row.setTagName(name);
        return row;
    }
}
