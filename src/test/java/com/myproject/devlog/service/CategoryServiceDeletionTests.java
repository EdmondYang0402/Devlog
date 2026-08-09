package com.myproject.devlog.service;

import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.common.UserContext;
import com.myproject.devlog.mapper.CategoryMapper;
import com.myproject.devlog.mapper.UserMapper;
import com.myproject.devlog.pojo.entity.Category;
import com.myproject.devlog.pojo.entity.User;
import com.myproject.devlog.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceDeletionTests {
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private CategoryTagService categoryTagService;
    @InjectMocks
    private CategoryServiceImpl service;

    @BeforeEach
    void authenticateAdmin() {
        UserContext.set(99L);
        User admin = new User();
        admin.setId(99L);
        admin.setRole(1);
        admin.setStatus(0);
        when(userMapper.selectById(99L)).thenReturn(admin);
    }

    @AfterEach
    void clearContext() {
        UserContext.clear();
    }

    @Test
    void deletingCategoryCleansRelationsBeforeDeletingCategory() {
        Category category = new Category();
        category.setId(7L);
        when(categoryMapper.selectById(7L)).thenReturn(category);
        when(categoryMapper.countArticlesByCategoryId(7L)).thenReturn(0L);
        when(categoryMapper.deleteById(7L)).thenReturn(1);

        service.delete(7L);

        InOrder order = inOrder(categoryTagService, categoryMapper);
        order.verify(categoryTagService).deleteByCategoryId(7L);
        order.verify(categoryMapper).deleteById(7L);
    }

    @Test
    void categoryReferencedByArticlesStillReturns409WithoutCleaningRelations() {
        Category category = new Category();
        category.setId(7L);
        when(categoryMapper.selectById(7L)).thenReturn(category);
        when(categoryMapper.countArticlesByCategoryId(7L)).thenReturn(2L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.delete(7L));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        verify(categoryTagService, never()).deleteByCategoryId(7L);
        verify(categoryMapper, never()).deleteById(7L);
    }
}
