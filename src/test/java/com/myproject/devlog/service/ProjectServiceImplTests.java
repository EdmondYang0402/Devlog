package com.myproject.devlog.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.mapper.ProjectMapper;
import com.myproject.devlog.pojo.dto.ProjectCreateDTO;
import com.myproject.devlog.pojo.dto.ProjectPageQueryDTO;
import com.myproject.devlog.pojo.dto.ProjectUpdateDTO;
import com.myproject.devlog.pojo.entity.ProjectShowcase;
import com.myproject.devlog.service.impl.ProjectServiceImpl;
import com.myproject.devlog.utils.ProjectConverter;
import com.myproject.devlog.utils.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProjectServiceImplTests {
    private ProjectMapper mapper;
    private ProjectServiceImpl service;

    @BeforeEach
    void setUp() {
        mapper = mock(ProjectMapper.class);
        ProjectValidator validator = new ProjectValidator();
        ProjectConverter converter = new ProjectConverter(validator, new ObjectMapper());
        service = new ProjectServiceImpl(mapper, converter);
    }

    @Test
    void createPersistsNormalizedEntityAndReturnsGeneratedId() {
        doAnswer(invocation -> {
            ProjectShowcase entity = invocation.getArgument(0);
            entity.setId(18L);
            return 1;
        }).when(mapper).insert(any(ProjectShowcase.class));

        assertEquals(18L, service.create(createDTO(" DevLog ")));

        ArgumentCaptor<ProjectShowcase> captor = ArgumentCaptor.forClass(ProjectShowcase.class);
        verify(mapper).insert(captor.capture());
        assertEquals("DevLog", captor.getValue().getName());
    }

    @Test
    void createRejectsUnexpectedAffectedRows() {
        when(mapper.insert(any(ProjectShowcase.class))).thenReturn(0);
        assertThrows(IllegalStateException.class, () -> service.create(createDTO("DevLog")));
    }

    @Test
    void missingProjectUsesNotFoundStatus() {
        when(mapper.selectById(9L)).thenReturn(null);
        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.getAdminById(9L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("项目不存在", exception.getMessage());
    }

    @Test
    void updatePreservesIdentityAndDeleteChecksExistence() {
        ProjectShowcase existing = entity(7L, 0);
        LocalDateTime created = LocalDateTime.of(2026, 1, 1, 0, 0);
        existing.setCreateTime(created);
        when(mapper.selectById(7L)).thenReturn(existing);
        when(mapper.updateById(existing)).thenReturn(1);

        ProjectUpdateDTO dto = new ProjectUpdateDTO();
        dto.setName("更新项目");
        dto.setSummary("更新简介");
        dto.setStatus(3);
        service.update(7L, dto);

        assertEquals(7L, existing.getId());
        assertEquals(created, existing.getCreateTime());
        verify(mapper).updateById(existing);

        when(mapper.deleteById(7L)).thenReturn(1);
        service.delete(7L);
        verify(mapper).deleteById(7L);
    }

    @Test
    void deletingMissingProjectReturnsNotFound() {
        when(mapper.selectById(8L)).thenReturn(null);
        BusinessException exception = assertThrows(BusinessException.class, () -> service.delete(8L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void adminPagePassesNormalizedKeywordStatusAndFeaturedFilters() {
        ProjectPageQueryDTO query = query(2, 10, 1, 1);
        query.setKeyword(" DevLog ");
        when(mapper.selectAdminPage(10, 10, "DevLog", 1, 1)).thenReturn(List.of(entity(1L, 1)));
        when(mapper.countAdmin("DevLog", 1, 1)).thenReturn(1L);

        assertEquals(1L, service.pageAdmin(query).getTotal());
        verify(mapper).selectAdminPage(10, 10, "DevLog", 1, 1);
        verify(mapper).countAdmin("DevLog", 1, 1);
    }

    @Test
    void frontPagePassesFiltersToDatabasePagination() {
        ProjectPageQueryDTO query = query(1, 12, 3, 0);
        when(mapper.selectFrontPage(0, 12, 3, 0)).thenReturn(List.of());
        when(mapper.countFront(3, 0)).thenReturn(0L);

        service.page(query);

        verify(mapper).selectFrontPage(0, 12, 3, 0);
        verify(mapper).countFront(3, 0);
    }

    @Test
    void featuredProjectsUseDefaultAndMaximumLimit() {
        when(mapper.selectFeaturedList(3)).thenReturn(List.of(entity(1L, 1)));
        assertEquals(1, service.listFeatured(null).size());
        verify(mapper).selectFeaturedList(3);

        when(mapper.selectFeaturedList(10)).thenReturn(List.of(entity(2L, 1)));
        assertEquals(1, service.listFeatured(99).size());
        verify(mapper).selectFeaturedList(10);
        assertThrows(BusinessException.class, () -> service.listFeatured(0));
    }

    private ProjectCreateDTO createDTO(String name) {
        ProjectCreateDTO dto = new ProjectCreateDTO();
        dto.setName(name);
        dto.setSummary("项目简介");
        dto.setStatus(1);
        return dto;
    }

    private ProjectPageQueryDTO query(int page, int size, Integer status, Integer featured) {
        ProjectPageQueryDTO query = new ProjectPageQueryDTO();
        query.setPage(page);
        query.setSize(size);
        query.setStatus(status);
        query.setFeatured(featured);
        return query;
    }

    private ProjectShowcase entity(Long id, Integer featured) {
        ProjectShowcase entity = new ProjectShowcase();
        entity.setId(id);
        entity.setName("项目" + id);
        entity.setSummary("简介");
        entity.setTechStack("[]");
        entity.setStatus(2);
        entity.setFeatured(featured);
        entity.setSortOrder(0);
        return entity;
    }
}
