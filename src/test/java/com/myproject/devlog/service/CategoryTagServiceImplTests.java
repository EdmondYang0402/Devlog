package com.myproject.devlog.service;

import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.mapper.CategoryMapper;
import com.myproject.devlog.mapper.CategoryTagMapper;
import com.myproject.devlog.mapper.TagMapper;
import com.myproject.devlog.pojo.entity.Category;
import com.myproject.devlog.pojo.entity.CategoryTag;
import com.myproject.devlog.pojo.entity.Tag;
import com.myproject.devlog.service.impl.CategoryTagServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryTagServiceImplTests {
    @Mock
    private CategoryTagMapper categoryTagMapper;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private TagMapper tagMapper;

    private CategoryTagServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CategoryTagServiceImpl(categoryTagMapper, categoryMapper, tagMapper);
    }

    @Test
    void setsFourTagsWithOneBatchValidationAndOneBatchInsert() {
        requireCategory(7L);
        List<Long> tagIds = List.of(1L, 2L, 3L, 4L);
        when(tagMapper.selectByIds(tagIds)).thenReturn(tags(tagIds));
        when(categoryTagMapper.insertBatch(any())).thenReturn(4);

        service.setTagsForCategory(7L, tagIds);

        ArgumentCaptor<Collection<CategoryTag>> captor = relationCaptor();
        verify(categoryTagMapper).insertBatch(captor.capture());
        assertEquals(tagIds, captor.getValue().stream().map(CategoryTag::getTagId).toList());
        assertEquals(List.of(7L, 7L, 7L, 7L),
                captor.getValue().stream().map(CategoryTag::getCategoryId).toList());
        verify(tagMapper).selectByIds(tagIds);
        verify(tagMapper, never()).selectById(any());
    }

    @Test
    void sameTagCanBeAssociatedWithTwoCategories() {
        requireCategory(1L);
        requireCategory(2L);
        when(tagMapper.selectByIds(List.of(9L))).thenReturn(tags(List.of(9L)));
        when(categoryTagMapper.insertBatch(any())).thenReturn(1);

        service.setTagsForCategory(1L, List.of(9L));
        service.setTagsForCategory(2L, List.of(9L));

        ArgumentCaptor<Collection<CategoryTag>> captor = relationCaptor();
        verify(categoryTagMapper, times(2)).insertBatch(captor.capture());
        assertEquals(1L, captor.getAllValues().get(0).iterator().next().getCategoryId());
        assertEquals(2L, captor.getAllValues().get(1).iterator().next().getCategoryId());
    }

    @Test
    void duplicateTagIdsAreDeduplicatedBeforeQueryAndInsert() {
        requireCategory(7L);
        when(tagMapper.selectByIds(List.of(2L, 3L))).thenReturn(tags(List.of(2L, 3L)));
        when(categoryTagMapper.insertBatch(any())).thenReturn(2);

        service.setTagsForCategory(7L, List.of(2L, 3L, 2L, 3L));

        verify(tagMapper).selectByIds(List.of(2L, 3L));
        ArgumentCaptor<Collection<CategoryTag>> captor = relationCaptor();
        verify(categoryTagMapper).insertBatch(captor.capture());
        assertEquals(2, captor.getValue().size());
    }

    @Test
    void emptyTagIdsOnlyClearExistingRelations() {
        requireCategory(7L);

        service.setTagsForCategory(7L, List.of());

        verify(categoryTagMapper).deleteByCategoryId(7L);
        verify(tagMapper, never()).selectByIds(any());
        verify(categoryTagMapper, never()).insertBatch(any());
    }

    @Test
    void missingCategoryReturns404BeforeRelationChanges() {
        when(categoryMapper.selectById(404L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.setTagsForCategory(404L, List.of(1L)));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("分类不存在", exception.getMessage());
        verify(categoryTagMapper, never()).deleteByCategoryId(any());
        verify(tagMapper, never()).selectByIds(any());
    }

    @Test
    void missingTagReturns404AndDoesNotDeleteOldRelations() {
        requireCategory(7L);
        when(tagMapper.selectByIds(List.of(1L, 12L))).thenReturn(tags(List.of(1L)));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.setTagsForCategory(7L, List.of(1L, 12L)));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("标签不存在：12", exception.getMessage());
        verify(tagMapper).selectByIds(List.of(1L, 12L));
        verify(tagMapper, never()).selectById(any());
        verify(categoryTagMapper, never()).deleteByCategoryId(any());
        verify(categoryTagMapper, never()).insertBatch(any());
    }

    @Test
    void replacementDeletesOldRelationsBeforeBatchInsert() {
        requireCategory(7L);
        when(tagMapper.selectByIds(List.of(2L, 3L))).thenReturn(tags(List.of(2L, 3L)));
        when(categoryTagMapper.insertBatch(any())).thenReturn(2);

        service.setTagsForCategory(7L, List.of(2L, 3L));

        InOrder order = inOrder(categoryTagMapper);
        order.verify(categoryTagMapper).deleteByCategoryId(7L);
        order.verify(categoryTagMapper).insertBatch(any());
    }

    @Test
    void queryReturnsAssociatedTagsWithoutChangingRelations() {
        requireCategory(7L);
        when(categoryTagMapper.selectTagsByCategoryId(7L))
                .thenReturn(List.of(tag(9L), tag(10L)));

        var result = service.listTagsByCategoryId(7L);

        assertEquals(List.of(9L, 10L), result.stream().map(item -> item.getId()).toList());
        verify(categoryTagMapper).selectTagsByCategoryId(7L);
        verify(categoryTagMapper, never()).deleteByCategoryId(any());
    }

    @Test
    void insertFailurePropagatesFromTransactionalReplacement() throws Exception {
        requireCategory(7L);
        when(tagMapper.selectByIds(List.of(1L, 2L))).thenReturn(tags(List.of(1L, 2L)));
        when(categoryTagMapper.insertBatch(any())).thenReturn(1);

        assertThrows(IllegalStateException.class,
                () -> service.setTagsForCategory(7L, List.of(1L, 2L)));

        Method method = CategoryTagServiceImpl.class
                .getMethod("setTagsForCategory", Long.class, List.class);
        Transactional transactional = method.getAnnotation(Transactional.class);
        assertNotNull(transactional);
        assertEquals(List.of(Exception.class), List.of(transactional.rollbackFor()));
    }

    @Test
    void cleanupMethodsDelegateToTheRelationMapper() {
        service.deleteByCategoryId(7L);
        service.deleteByTagId(9L);

        verify(categoryTagMapper).deleteByCategoryId(7L);
        verify(categoryTagMapper).deleteByTagId(9L);
    }

    private void requireCategory(Long categoryId) {
        Category category = new Category();
        category.setId(categoryId);
        when(categoryMapper.selectById(categoryId)).thenReturn(category);
    }

    private List<Tag> tags(List<Long> ids) {
        return ids.stream().map(this::tag).toList();
    }

    private Tag tag(Long id) {
        Tag tag = new Tag();
        tag.setId(id);
        tag.setName("Tag " + id);
        return tag;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private ArgumentCaptor<Collection<CategoryTag>> relationCaptor() {
        return (ArgumentCaptor) ArgumentCaptor.forClass(Collection.class);
    }
}
