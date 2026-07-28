package com.myproject.devlog.service;

import com.myproject.devlog.mapper.ArticleMapper;
import com.myproject.devlog.pojo.dto.ArticleCreateDTO;
import com.myproject.devlog.pojo.entity.Article;
import com.myproject.devlog.pojo.vo.ArticleDetailVO;
import com.myproject.devlog.service.impl.ArticleServiceImpl;
import com.myproject.devlog.utils.ArticleConverter;
import org.apache.ibatis.annotations.Select;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static com.myproject.devlog.common.ArticleStatusConstant.DRAFT;
import static com.myproject.devlog.common.ArticleStatusConstant.PUBLISHED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ArticleStatusTests {
    @Test
    void newArticleDefaultsToDraftAndKeepsExplicitPublishedStatus() {
        ArticleCreateDTO draftDto = new ArticleCreateDTO();
        assertEquals(DRAFT, ArticleConverter.fromCreateDTO(draftDto, 1L).getStatus());

        ArticleCreateDTO publishedDto = new ArticleCreateDTO();
        publishedDto.setStatus(PUBLISHED);
        assertEquals(PUBLISHED, ArticleConverter.fromCreateDTO(publishedDto, 1L).getStatus());
    }

    @Test
    void unavailableDraftDetailDoesNotIncreaseViewCount() {
        ArticleMapper mapper = mock(ArticleMapper.class);
        ArticleTagService articleTagService = mock(ArticleTagService.class);
        when(mapper.selectFrontDetailById(8L)).thenReturn(null);
        ArticleServiceImpl service = new ArticleServiceImpl(mapper, articleTagService);

        assertNull(service.getFrontDetail(8L));
        verify(mapper, never()).updateViewCount(8L);
    }

    @Test
    void publishedDetailIncreasesViewCountAfterVisibilityCheck() {
        ArticleMapper mapper = mock(ArticleMapper.class);
        ArticleTagService articleTagService = mock(ArticleTagService.class);
        ArticleDetailVO detail = new ArticleDetailVO();
        detail.setViewCount(3L);
        when(mapper.selectFrontDetailById(9L)).thenReturn(detail);
        ArticleServiceImpl service = new ArticleServiceImpl(mapper, articleTagService);

        assertEquals(4L, service.getFrontDetail(9L).getViewCount());
        verify(mapper).updateViewCount(9L);
    }

    @Test
    void publicMapperQueriesExplicitlyRequirePublishedStatus() throws Exception {
        for (String methodName : new String[]{"frontPage", "countFront", "selectFrontDetailById"}) {
            Method method = Arrays.stream(ArticleMapper.class.getDeclaredMethods())
                    .filter(candidate -> candidate.getName().equals(methodName))
                    .findFirst().orElseThrow();
            String sql = String.join(" ", method.getAnnotation(Select.class).value());
            assertTrue(sql.matches("(?s).*status\\s*=\\s*1.*"), methodName + " must filter published articles");
        }
    }
}
