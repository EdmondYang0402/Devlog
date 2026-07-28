package com.myproject.devlog.controller;

import com.myproject.devlog.common.PageResult;
import com.myproject.devlog.pojo.dto.SiteBackgroundCreateDTO;
import com.myproject.devlog.pojo.dto.SiteBackgroundPageQueryDTO;
import com.myproject.devlog.pojo.vo.SiteBackgroundAdminVO;
import com.myproject.devlog.pojo.vo.SiteBackgroundPublicVO;
import com.myproject.devlog.service.SiteBackgroundService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SiteBackgroundControllerTests {

    @Test
    void publicRouteUsesUnifiedResultAndDoesNotExposeManagementFields() throws Exception {
        SiteBackgroundService service = mock(SiteBackgroundService.class);
        SiteBackgroundPublicVO vo = new SiteBackgroundPublicVO();
        vo.setId(1L);
        vo.setImageUrl("https://cdn.example.com/bg.webp");
        vo.setTitle("背景");
        when(service.getEnabledBackgrounds()).thenReturn(List.of(vo));
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new SiteBackgroundController(service)).build();

        mvc.perform(get("/site/backgrounds"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].imageUrl").value("https://cdn.example.com/bg.webp"))
                .andExpect(jsonPath("$.data[0].enabled").doesNotExist())
                .andExpect(jsonPath("$.data[0].sortOrder").doesNotExist());
    }

    @Test
    void adminCrudAndPageRoutesDelegateToService() throws Exception {
        SiteBackgroundService service = mock(SiteBackgroundService.class);
        when(service.adminPage(any(SiteBackgroundPageQueryDTO.class)))
                .thenReturn(new PageResult<>(List.of(), 0));
        when(service.getAdminDetail(7L)).thenReturn(new SiteBackgroundAdminVO());
        when(service.create(any(SiteBackgroundCreateDTO.class))).thenReturn(12L);
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new AdminSiteBackgroundController(service)).build();

        mvc.perform(get("/admin/site-backgrounds").param("page", "1").param("size", "10"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
        mvc.perform(get("/admin/site-backgrounds/7"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
        mvc.perform(post("/admin/site-backgrounds").contentType("application/json")
                        .content("""
                                {"imageUrl":"https://cdn.example.com/bg.webp","enabled":1,"sortOrder":2}
                                """))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data").value(12));
        mvc.perform(put("/admin/site-backgrounds/7").contentType("application/json")
                        .content("""
                                {"imageUrl":"https://cdn.example.com/new.webp","enabled":0,"sortOrder":1}
                                """))
                .andExpect(status().isOk());
        mvc.perform(delete("/admin/site-backgrounds/7")).andExpect(status().isOk());

        verify(service).getAdminDetail(7L);
        verify(service).delete(7L);
    }
}
