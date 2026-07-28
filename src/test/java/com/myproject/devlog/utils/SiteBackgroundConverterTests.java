package com.myproject.devlog.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.devlog.pojo.dto.SiteBackgroundCreateDTO;
import com.myproject.devlog.pojo.dto.SiteBackgroundUpdateDTO;
import com.myproject.devlog.pojo.entity.SiteBackground;
import com.myproject.devlog.pojo.vo.SiteBackgroundAdminVO;
import com.myproject.devlog.pojo.vo.SiteBackgroundPublicVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SiteBackgroundConverterTests {
    private SiteBackgroundConverter converter;

    @BeforeEach
    void setUp() {
        converter = new SiteBackgroundConverter(new SiteBackgroundValidator());
    }

    @Test
    void createNormalizesDtoIntoEntity() {
        SiteBackgroundCreateDTO dto = new SiteBackgroundCreateDTO();
        dto.setImageUrl(" https://cdn.example.com/bg.webp ");
        dto.setTitle(" 首页背景 ");

        SiteBackground entity = converter.fromCreateDTO(dto);

        assertEquals("https://cdn.example.com/bg.webp", entity.getImageUrl());
        assertEquals("首页背景", entity.getTitle());
        assertEquals(1, entity.getEnabled());
        assertEquals(0, entity.getSortOrder());
    }

    @Test
    void applyUpdatePreservesIdAndCreateTime() {
        SiteBackground entity = entity();
        LocalDateTime created = LocalDateTime.of(2026, 7, 1, 9, 0);
        entity.setCreateTime(created);
        SiteBackgroundUpdateDTO dto = new SiteBackgroundUpdateDTO();
        dto.setImageUrl("https://cdn.example.com/new.webp");
        dto.setTitle("新背景");
        dto.setEnabled(0);
        dto.setSortOrder(9);

        converter.applyUpdate(entity, dto);

        assertEquals(7L, entity.getId());
        assertEquals(created, entity.getCreateTime());
        assertEquals("新背景", entity.getTitle());
    }

    @Test
    void adminVoContainsManagementFieldsButPublicVoDoesNot() throws Exception {
        SiteBackground entity = entity();
        entity.setEnabled(1);
        entity.setSortOrder(8);
        entity.setCreateTime(LocalDateTime.of(2026, 7, 1, 9, 0));
        entity.setUpdateTime(LocalDateTime.of(2026, 7, 2, 9, 0));

        SiteBackgroundAdminVO adminVO = converter.toAdminVO(entity);
        SiteBackgroundPublicVO publicVO = converter.toPublicVO(entity);
        assertEquals(1, adminVO.getEnabled());
        assertEquals(8, adminVO.getSortOrder());

        String publicJson = new ObjectMapper().writeValueAsString(publicVO);
        assertTrue(publicJson.contains("imageUrl"));
        assertFalse(publicJson.contains("enabled"));
        assertFalse(publicJson.contains("sortOrder"));
        assertFalse(publicJson.contains("createTime"));
        assertFalse(publicJson.contains("updateTime"));
    }

    private SiteBackground entity() {
        SiteBackground entity = new SiteBackground();
        entity.setId(7L);
        entity.setImageUrl("https://cdn.example.com/bg.webp");
        entity.setTitle("背景");
        return entity;
    }
}
