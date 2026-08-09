package com.myproject.devlog.service;

import com.myproject.devlog.mapper.ArticleTagMapper;
import com.myproject.devlog.mapper.TagMapper;
import com.myproject.devlog.pojo.entity.Tag;
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
        when(articleTagMapper.selectTagsByArticleIds(List.of(10L, 20L)))
                .thenReturn(List.of(row(10L, 1L, "Java"), row(10L, 2L, "JWT")));

        Map<Long, List<TagVO>> result = service.listByArticleIds(List.of(10L, 20L, 10L));

        assertEquals(List.of("Java", "JWT"), result.get(10L).stream().map(TagVO::getName).toList());
        assertFalse(result.containsKey(20L));
        verify(articleTagMapper, times(1)).selectTagsByArticleIds(List.of(10L, 20L));
        verify(articleTagMapper, never()).selectTagsByArticleId(anyLong());
    }

    @Test
    void emptyBatchDoesNotQueryMapper() {
        assertTrue(service.listByArticleIds(List.of()).isEmpty());
        verifyNoInteractions(articleTagMapper, tagMapper);
    }

    @Test
    void emptyMapperResultReturnsEmptyMap() {
        when(articleTagMapper.selectTagsByArticleIds(List.of(10L))).thenReturn(List.of());
        assertTrue(service.listByArticleIds(List.of(10L)).isEmpty());
    }

    @Test
    void deleteByArticleIdUsesExplicitCleanupForNoForeignKeySchema() {
        service.deleteByArticleId(10L);
        verify(articleTagMapper).deleteByArticleId(10L);
    }

    @Test
    void singleArticleListReturnsConvertedTags() {
        Tag tag = new Tag();
        tag.setId(2L);
        tag.setName("Java");
        when(articleTagMapper.selectByArticleId(1L)).thenReturn(List.of(tag));

        assertEquals("Java", service.listByArticleId(1L).getFirst().getName());
        verify(articleTagMapper).selectByArticleId(1L);
    }

    @Test
    void replaceValidatesTagsAndPersistsDistinctRelations() {
        when(tagMapper.countByIds(List.of(2L, 3L))).thenReturn(2L);

        when(articleTagMapper.insertBatch(any())).thenReturn(2);

        service.replaceArticleTags(1L, List.of(2L, 2L, 3L));

        verify(articleTagMapper).deleteByArticleId(1L);
        verify(articleTagMapper).insertBatch(argThat(relations ->
                relations.size() == 2
                        && relations.stream().allMatch(relation -> relation.getArticleId().equals(1L))
                        && relations.stream().map(relation -> relation.getTagId()).toList()
                        .equals(List.of(2L, 3L))));
    }

    private ArticleTagQueryVO row(Long articleId, Long tagId, String name) {
        ArticleTagQueryVO row = new ArticleTagQueryVO();
        row.setArticleId(articleId);
        row.setTagId(tagId);
        row.setTagName(name);
        return row;
    }
}
