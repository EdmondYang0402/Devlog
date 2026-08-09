package com.myproject.devlog.controller;

import com.myproject.devlog.common.PageResult;
import com.myproject.devlog.pojo.dto.ProjectCreateDTO;
import com.myproject.devlog.pojo.dto.ProjectPageQueryDTO;
import com.myproject.devlog.pojo.vo.ProjectDetailVO;
import com.myproject.devlog.pojo.vo.ProjectListVO;
import com.myproject.devlog.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectControllerTests {

    @Test
    void frontPageDetailAndFeaturedRoutesReturnUnifiedResults() throws Exception {
        ProjectService service = mock(ProjectService.class);
        when(service.page(any(ProjectPageQueryDTO.class)))
                .thenReturn(new PageResult<>(List.of(), 0));
        when(service.listFeatured(5)).thenReturn(List.of());
        when(service.getById(7L)).thenReturn(new ProjectDetailVO());
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new ProjectController(service)).build();

        mvc.perform(get("/projects").param("page", "1").param("size", "12"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
        mvc.perform(get("/projects/featured").param("limit", "5"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
        mvc.perform(get("/projects/7"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));

        verify(service).listFeatured(5);
        verify(service).getById(7L);
    }

    @Test
    void adminCreateReturnsGeneratedIdThroughExistingResultStructure() throws Exception {
        ProjectService service = mock(ProjectService.class);
        when(service.create(any(ProjectCreateDTO.class))).thenReturn(12L);
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new AdminProjectController(service)).build();

        mvc.perform(post("/admin/projects")
                        .contentType("application/json")
                        .content("""
                                {"name":"DevLog","summary":"个人项目","status":1,"techStack":["Java"]}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(12));
    }
}
