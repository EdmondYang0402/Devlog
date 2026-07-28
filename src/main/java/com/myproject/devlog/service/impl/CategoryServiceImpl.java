package com.myproject.devlog.service.impl;

import com.myproject.devlog.common.UserContext;
import com.myproject.devlog.mapper.CategoryMapper;
import com.myproject.devlog.mapper.UserMapper;
import com.myproject.devlog.pojo.dto.CategoryCreateDTO;
import com.myproject.devlog.pojo.dto.CategoryUpdateDTO;
import com.myproject.devlog.pojo.entity.Category;
import com.myproject.devlog.pojo.entity.User;
import com.myproject.devlog.pojo.vo.CategoryVO;
import com.myproject.devlog.service.CategoryService;
import com.myproject.devlog.utils.CategoryConverter;
import com.myproject.devlog.utils.PermissionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    UserMapper userMapper;
    @Override
    public void create(CategoryCreateDTO dto) {
        requireAdmin();
        String name = dto.getName().trim();
        Category existing = categoryMapper.getByName(name);
        if (existing != null) {
            throw new RuntimeException("分类名称已存在");
        }

        Category category = CategoryConverter.toEntity(dto);
        category.setName(name);

        categoryMapper.insert(category);
    }

    @Override
    public void update(Long id, CategoryUpdateDTO dto) {
        requireAdmin();

        Category category = categoryMapper.getById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }

        String name = dto.getName().trim();

        Category existing = categoryMapper.getByName(name);
        if (existing != null && !existing.getId().equals(id)) {
            throw new RuntimeException("分类名称已存在");
        }

        CategoryConverter.updateEntity(category, dto);
        category.setName(name);

        categoryMapper.update(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        requireAdmin();

        Category category = categoryMapper.getById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }

        Long articleCount = categoryMapper.countArticlesByCategoryId(id);
        if (articleCount != null && articleCount > 0) {
            throw new RuntimeException("该分类下仍有文章，无法删除");
        }

        int affectedRows = categoryMapper.deleteById(id);
        if (affectedRows == 0) {
            throw new RuntimeException("分类删除失败");
        }
    }

    @Override
    public List<CategoryVO> listAll() {
        List<Category> categories = categoryMapper.listAll();
        List<CategoryVO> result = new ArrayList<>();

        for (Category category : categories) {
            Long articleCount =
                    categoryMapper.countArticlesByCategoryId(category.getId());

            result.add(CategoryConverter.toVO(category, articleCount));
        }

        return result;
    }
    private User requireAdmin() {
        Long userId = UserContext.get();

        if (userId == null) {
            throw new RuntimeException("未登录");
        }

        User user = userMapper.getById(userId);
        PermissionUtil.checkAdmin(user);

        return user;
    }
}
