package com.myproject.devlog.service;

import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.mapper.MediaReviewMapper;
import com.myproject.devlog.pojo.dto.MediaReviewCreateDTO;
import com.myproject.devlog.pojo.dto.MediaReviewUpdateDTO;
import com.myproject.devlog.pojo.entity.MediaReview;
import com.myproject.devlog.service.impl.MediaReviewServiceImpl;
import com.myproject.devlog.utils.MediaReviewConverter;
import com.myproject.devlog.utils.MediaReviewValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MediaReviewServiceImplTests {
    private MediaReviewMapper mapper;
    private MediaReviewServiceImpl service;

    @BeforeEach
    void setUp() {
        mapper = mock(MediaReviewMapper.class);
        MediaReviewValidator validator = new MediaReviewValidator();
        service = new MediaReviewServiceImpl(mapper, validator, new MediaReviewConverter(validator));
    }

    @Test
    void createNormalizesAndPersistsEntity() {
        when(mapper.insert(org.mockito.ArgumentMatchers.any())).thenReturn(1);
        MediaReviewCreateDTO dto = createDTO("  作品  ");

        service.create(dto);

        ArgumentCaptor<MediaReview> captor = ArgumentCaptor.forClass(MediaReview.class);
        verify(mapper).insert(captor.capture());
        assertEquals("作品", captor.getValue().getTitle());
    }

    @Test
    void createRejectsUnexpectedAffectedRows() {
        when(mapper.insert(org.mockito.ArgumentMatchers.any())).thenReturn(0);
        assertThrows(BusinessException.class, () -> service.create(createDTO("作品")));
    }

    @Test
    void missingDetailUsesNotFoundStatus() {
        when(mapper.selectById(9L)).thenReturn(null);
        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.getAdminDetail(9L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void updatePreservesIdentityAndAppliesValidatedFields() {
        MediaReview existing = entity(7L, "旧标题");
        when(mapper.selectById(7L)).thenReturn(existing);
        when(mapper.update(existing)).thenReturn(1);
        MediaReviewUpdateDTO dto = updateDTO(" 新标题 ");

        service.update(7L, dto);

        assertEquals(7L, existing.getId());
        assertEquals("新标题", existing.getTitle());
        verify(mapper).update(existing);
    }

    @Test
    void deleteSucceedsAndMissingDeleteReturnsNotFound() {
        when(mapper.selectById(7L)).thenReturn(entity(7L, "作品"));
        when(mapper.deleteById(7L)).thenReturn(1);
        service.delete(7L);
        verify(mapper).deleteById(7L);

        when(mapper.selectById(8L)).thenReturn(null);
        BusinessException exception = assertThrows(BusinessException.class, () -> service.delete(8L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void adminPagePassesNormalizedFiltersToMapper() {
        when(mapper.adminPage(10, 10, "关键词", 1, 2)).thenReturn(List.of(entity(1L, "作品")));
        when(mapper.adminCount("关键词", 1, 2)).thenReturn(1L);

        var result = service.adminPage(2, 10, " 关键词 ", 1, 2);

        assertEquals(1, result.getRecords().size());
        assertEquals(1L, result.getTotal());
        verify(mapper).adminPage(10, 10, "关键词", 1, 2);
    }

    @Test
    void frontPagePassesTypeAndSortToDatabaseQuery() {
        when(mapper.frontPage(0, 12, 3, "rating")).thenReturn(List.of());
        when(mapper.frontCount(3)).thenReturn(0L);

        service.getFrontPage(1, 12, 3, "rating");

        verify(mapper).frontPage(0, 12, 3, "rating");
        verify(mapper).frontCount(3);
        assertThrows(BusinessException.class,
                () -> service.getFrontPage(1, 12, 3, "unknown"));
    }

    private MediaReviewCreateDTO createDTO(String title) {
        MediaReviewCreateDTO dto = new MediaReviewCreateDTO();
        dto.setTitle(title);
        dto.setMediaType(0);
        dto.setStatus(2);
        dto.setRating(9);
        return dto;
    }

    private MediaReviewUpdateDTO updateDTO(String title) {
        MediaReviewUpdateDTO dto = new MediaReviewUpdateDTO();
        dto.setTitle(title);
        dto.setMediaType(1);
        dto.setStatus(2);
        dto.setRating(null);
        return dto;
    }

    private MediaReview entity(Long id, String title) {
        MediaReview entity = new MediaReview();
        entity.setId(id);
        entity.setTitle(title);
        entity.setMediaType(0);
        entity.setStatus(2);
        return entity;
    }
}
