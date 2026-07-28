package com.myproject.devlog.controller;

import com.myproject.devlog.pojo.vo.SiteProfileVO;
import com.myproject.devlog.service.SiteConfigService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SiteProfileControllerTests {
    @Test
    void publicProfileReturnsSiteDataWithoutAuthentication() throws Exception {
        SiteConfigService service = mock(SiteConfigService.class);
        when(service.getPublicProfile()).thenReturn(SiteProfileVO.builder()
                .siteTitle("DevLog").heroKeywords(List.of("Java")).build());
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new SiteProfileController(service)).build();

        mvc.perform(get("/site/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.siteTitle").value("DevLog"))
                .andExpect(jsonPath("$.data.heroKeywords[0]").value("Java"));
    }
}
