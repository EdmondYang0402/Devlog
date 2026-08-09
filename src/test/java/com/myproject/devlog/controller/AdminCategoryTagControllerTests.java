package com.myproject.devlog.controller;

import com.myproject.devlog.common.GlobalExceptionHandler;
import com.myproject.devlog.pojo.vo.TagVO;
import com.myproject.devlog.service.CategoryTagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminCategoryTagControllerTests {
    private CategoryTagService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(CategoryTagService.class);
        mvc = MockMvcBuilders.standaloneSetup(new AdminCategoryTagController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void listsTagsForCategory() throws Exception {
        TagVO tag = new TagVO();
        tag.setId(9L);
        tag.setName("HashMap");
        when(service.listTagsByCategoryId(7L)).thenReturn(List.of(tag));

        mvc.perform(get("/admin/categories/7/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(9))
                .andExpect(jsonPath("$.data[0].name").value("HashMap"));

        verify(service).listTagsByCategoryId(7L);
    }

    @Test
    void replacesCategoryTagsWithOneRequest() throws Exception {
        mvc.perform(put("/admin/categories/7/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tagIds\":[1,2,3,4]}"))
                .andExpect(status().isOk());

        verify(service).setTagsForCategory(7L, List.of(1L, 2L, 3L, 4L));
    }

    @Test
    void emptyArrayIsAllowedForClearingRelations() throws Exception {
        mvc.perform(put("/admin/categories/7/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tagIds\":[]}"))
                .andExpect(status().isOk());

        verify(service).setTagsForCategory(7L, List.of());
    }

    @Test
    void nullOrNonPositiveTagIdReturns400WithoutCallingService() throws Exception {
        mvc.perform(put("/admin/categories/7/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tagIds\":[1,null,0]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        verifyNoInteractions(service);
    }

    @Test
    void missingTagIdsReturns400WithoutCallingService() throws Exception {
        mvc.perform(put("/admin/categories/7/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        verifyNoInteractions(service);
    }
}
