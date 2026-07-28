package com.myproject.devlog.service;

import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.mapper.TagMapper;
import com.myproject.devlog.pojo.dto.TagCreateDTO;
import com.myproject.devlog.pojo.dto.TagUpdateDTO;
import com.myproject.devlog.pojo.entity.Tag;
import com.myproject.devlog.pojo.vo.AdminTagVO;
import com.myproject.devlog.service.impl.TagServiceImpl;
import com.myproject.devlog.utils.TagConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTests {
    @Mock
    private TagMapper tagMapper;
    private TagServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new TagServiceImpl(tagMapper);
    }

    @Test
    void deleteRejectsMissingTag() {
        when(tagMapper.selectById(5L)).thenReturn(null);
        BusinessException exception = assertThrows(BusinessException.class, () -> service.delete(5L));
        assertEquals("标签不存在", exception.getMessage());
        verify(tagMapper, never()).deleteById(anyLong());
    }

    @Test
    void deleteRejectsReferencedTagWithoutRemovingRelations() {
        Tag tag = tag(6L, "Java");
        when(tagMapper.selectById(6L)).thenReturn(tag);
        when(tagMapper.countArticleReferences(6L)).thenReturn(2L);
        BusinessException exception = assertThrows(BusinessException.class, () -> service.delete(6L));
        assertEquals("该标签仍被文章引用，无法删除", exception.getMessage());
        verify(tagMapper, never()).deleteById(anyLong());
    }

    @Test
    void deleteUnreferencedTag() {
        when(tagMapper.selectById(7L)).thenReturn(tag(7L, "JWT"));
        when(tagMapper.countArticleReferences(7L)).thenReturn(0L);
        when(tagMapper.deleteById(7L)).thenReturn(1);
        service.delete(7L);
        verify(tagMapper).deleteById(7L);
    }

    @Test
    void listMethodsReturnConvertedAndAggregatedValues() {
        when(tagMapper.listAll()).thenReturn(List.of(tag(1L, "Java")));
        AdminTagVO admin = new AdminTagVO();
        admin.setId(1L);
        admin.setArticleCount(3L);
        when(tagMapper.adminListWithArticleCount()).thenReturn(List.of(admin));

        assertEquals("Java", service.listAll().getFirst().getName());
        assertEquals(3L, service.adminList().getFirst().getArticleCount());
    }

    @Test
    void converterCopiesMechanicalFields() {
        TagCreateDTO dto = new TagCreateDTO();
        dto.setName("Spring Boot");
        assertEquals("Spring Boot", TagConverter.fromCreateDTO(dto).getName());
        assertEquals("Java", TagConverter.toVO(tag(1L, "Java")).getName());
    }

    @Test
    void updateNormalizesNameAndPersistsExistingTag() {
        Tag existing = tag(1L, "Java");
        TagUpdateDTO dto = new TagUpdateDTO();
        dto.setName(" Spring Boot ");
        when(tagMapper.selectById(1L)).thenReturn(existing);
        when(tagMapper.existsByNameExcludeId("Spring Boot", 1L)).thenReturn(false);

        service.update(1L, dto);

        assertEquals("Spring Boot", existing.getName());
        verify(tagMapper).update(existing);
    }

    private Tag tag(Long id, String name) {
        Tag tag = new Tag();
        tag.setId(id);
        tag.setName(name);
        return tag;
    }
}
