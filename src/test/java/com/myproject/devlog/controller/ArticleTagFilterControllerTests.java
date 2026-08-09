package com.myproject.devlog.controller;

import com.myproject.devlog.common.PageResult;
import com.myproject.devlog.service.ArticleService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ArticleTagFilterControllerTests {
    @Test
    void repeatedTagIdsBindToLongList() throws Exception {
        ArticleService service = mock(ArticleService.class);
        when(service.page(1, 10, 2L, "notes", "JWT", List.of(3L, 5L)))
                .thenReturn(new PageResult<>(List.of(), 0));
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new ArticleController(service)).build();

        mvc.perform(get("/articles")
                        .param("page", "1")
                        .param("size", "10")
                        .param("categoryId", "2")
                        .param("categorySlug", "notes")
                        .param("keyword", "JWT")
                        .param("tagIds", "3", "5"))
                .andExpect(status().isOk());

        verify(service).page(1, 10, 2L, "notes", "JWT", List.of(3L, 5L));
    }
}
