package com.myproject.devlog.service;

import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.mapper.SiteBackgroundMapper;
import com.myproject.devlog.pojo.dto.SiteBackgroundCreateDTO;
import com.myproject.devlog.pojo.dto.SiteBackgroundPageQueryDTO;
import com.myproject.devlog.pojo.dto.SiteBackgroundUpdateDTO;
import com.myproject.devlog.pojo.entity.SiteBackground;
import com.myproject.devlog.service.impl.SiteBackgroundServiceImpl;
import com.myproject.devlog.utils.SiteBackgroundConverter;
import com.myproject.devlog.utils.SiteBackgroundValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SiteBackgroundServiceImplTests {
    private SiteBackgroundMapper mapper;
    private SiteBackgroundServiceImpl service;

    @BeforeEach
    void setUp() {
        mapper = mock(SiteBackgroundMapper.class);
        SiteBackgroundValidator validator = new SiteBackgroundValidator();
        service = new SiteBackgroundServiceImpl(mapper, new SiteBackgroundConverter(validator));
    }

    @Test
    void createPersistsNormalizedEntityAndReturnsGeneratedId() {
        doAnswer(invocation -> {
            SiteBackground entity = invocation.getArgument(0);
            entity.setId(18L);
            return 1;
        }).when(mapper).insert(any(SiteBackground.class));

        assertEquals(18L, service.create(createDTO(" https://cdn.example.com/bg.webp ")));
        ArgumentCaptor<SiteBackground> captor = ArgumentCaptor.forClass(SiteBackground.class);
        verify(mapper).insert(captor.capture());
        assertEquals("https://cdn.example.com/bg.webp", captor.getValue().getImageUrl());
    }

    @Test
    void createRejectsUnexpectedAffectedRows() {
        when(mapper.insert(any(SiteBackground.class))).thenReturn(0);
        assertThrows(IllegalStateException.class,
                () -> service.create(createDTO("https://cdn.example.com/bg.webp")));
    }

    @Test
    void missingBackgroundUsesNotFoundStatus() {
        when(mapper.selectById(9L)).thenReturn(null);
        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.getAdminById(9L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("背景图片记录不存在", exception.getMessage());
    }

    @Test
    void updateAndDeleteCheckExistingRecordAndAffectedRows() {
        SiteBackground existing = entity(7L, 1, 0);
        when(mapper.selectById(7L)).thenReturn(existing);
        when(mapper.updateById(existing)).thenReturn(1);
        SiteBackgroundUpdateDTO dto = new SiteBackgroundUpdateDTO();
        dto.setImageUrl("https://cdn.example.com/new.webp");
        dto.setEnabled(0);
        service.update(7L, dto);
        verify(mapper).updateById(existing);

        when(mapper.deleteById(7L)).thenReturn(1);
        service.delete(7L);
        verify(mapper).deleteById(7L);
    }

    @Test
    void deletingMissingBackgroundReturnsNotFound() {
        when(mapper.selectById(8L)).thenReturn(null);
        BusinessException exception = assertThrows(BusinessException.class, () -> service.delete(8L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void adminPagePassesNormalizedKeywordAndEnabledToDatabase() {
        SiteBackgroundPageQueryDTO query = new SiteBackgroundPageQueryDTO();
        query.setPage(2);
        query.setSize(10);
        query.setKeyword(" 首页 ");
        query.setEnabled(1);
        when(mapper.selectAdminPage(10, 10, "首页", 1)).thenReturn(List.of(entity(1L, 1, 3)));
        when(mapper.countAdmin("首页", 1)).thenReturn(1L);

        assertEquals(1L, service.pageAdmin(query).getTotal());
        verify(mapper).selectAdminPage(10, 10, "首页", 1);
        verify(mapper).countAdmin("首页", 1);
    }

    @Test
    void enabledListUsesDedicatedMapperQueryAndPublicVo() {
        when(mapper.selectEnabledList()).thenReturn(List.of(entity(2L, 1, 9)));
        var result = service.listEnabled();
        assertEquals(1, result.size());
        assertEquals(2L, result.getFirst().getId());
        verify(mapper).selectEnabledList();
    }

    @Test
    void enabledListReturnsEmptyListInsteadOfNull() {
        when(mapper.selectEnabledList()).thenReturn(null);
        assertEquals(List.of(), service.listEnabled());
    }

    private SiteBackgroundCreateDTO createDTO(String imageUrl) {
        SiteBackgroundCreateDTO dto = new SiteBackgroundCreateDTO();
        dto.setImageUrl(imageUrl);
        dto.setTitle("背景");
        return dto;
    }

    private SiteBackground entity(Long id, Integer enabled, Integer sortOrder) {
        SiteBackground entity = new SiteBackground();
        entity.setId(id);
        entity.setImageUrl("https://cdn.example.com/" + id + ".webp");
        entity.setTitle("背景" + id);
        entity.setEnabled(enabled);
        entity.setSortOrder(sortOrder);
        return entity;
    }
}
