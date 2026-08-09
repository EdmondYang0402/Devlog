package com.myproject.devlog.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.pojo.dto.ProjectCreateDTO;
import com.myproject.devlog.pojo.dto.ProjectUpdateDTO;
import com.myproject.devlog.pojo.entity.ProjectShowcase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProjectConverterTests {
    private ProjectConverter converter;

    @BeforeEach
    void setUp() {
        converter = new ProjectConverter(new ProjectValidator(), new ObjectMapper());
    }

    @Test
    void createSerializesTechStackAsJsonAndDetailRestoresList() {
        ProjectShowcase entity = converter.fromCreateDTO(createDTO(List.of("Java", "Spring Boot")));
        assertEquals("[\"Java\",\"Spring Boot\"]", entity.getTechStack());
        assertEquals(List.of("Java", "Spring Boot"), converter.toDetailVO(entity).getTechStack());
    }

    @Test
    void applyUpdatePreservesIdAndCreateTime() {
        ProjectShowcase entity = new ProjectShowcase();
        entity.setId(9L);
        LocalDateTime createTime = LocalDateTime.of(2026, 7, 1, 12, 0);
        entity.setCreateTime(createTime);

        ProjectUpdateDTO dto = new ProjectUpdateDTO();
        dto.setName("新名称");
        dto.setSummary("新简介");
        dto.setStatus(3);

        converter.applyUpdate(entity, dto);

        assertEquals(9L, entity.getId());
        assertEquals(createTime, entity.getCreateTime());
        assertEquals("新名称", entity.getName());
    }

    @Test
    void emptyTechStackUsesNullInDatabaseAndEmptyListInApi() {
        ProjectShowcase entity = converter.fromCreateDTO(createDTO(List.of(" ")));
        assertNull(entity.getTechStack());
        assertEquals(List.of(), converter.toListVO(entity).getTechStack());
    }

    @Test
    void invalidJsonIsNotSilentlyConvertedToEmptyList() {
        ProjectShowcase entity = new ProjectShowcase();
        entity.setTechStack("not-json");
        assertThrows(IllegalStateException.class, () -> converter.toDetailVO(entity));
    }

    @Test
    void serializedTechStackCannotExceedDatabaseColumn() {
        List<String> escapedValues = java.util.stream.IntStream.range(0, 20)
                .mapToObj(index -> index + ":" + "\\\"".repeat(23))
                .toList();
        assertThrows(BusinessException.class,
                () -> converter.fromCreateDTO(createDTO(escapedValues)));
    }

    private ProjectCreateDTO createDTO(List<String> techStack) {
        ProjectCreateDTO dto = new ProjectCreateDTO();
        dto.setName("DevLog");
        dto.setSummary("个人开发日志项目");
        dto.setStatus(1);
        dto.setTechStack(techStack);
        return dto;
    }
}
