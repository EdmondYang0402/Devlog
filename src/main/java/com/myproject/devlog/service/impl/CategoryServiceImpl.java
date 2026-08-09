package com.myproject.devlog.service.impl;

import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.common.UserContext;
import com.myproject.devlog.mapper.CategoryMapper;
import com.myproject.devlog.mapper.UserMapper;
import com.myproject.devlog.pojo.dto.CategoryCreateDTO;
import com.myproject.devlog.pojo.dto.CategoryUpdateDTO;
import com.myproject.devlog.pojo.entity.Category;
import com.myproject.devlog.pojo.entity.User;
import com.myproject.devlog.pojo.vo.CategoryVO;
import com.myproject.devlog.service.CategoryService;
import com.myproject.devlog.service.CategoryTagService;
import com.myproject.devlog.utils.CategoryConverter;
import com.myproject.devlog.utils.PermissionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    CategoryTagService categoryTagService;
    @Override
    public void create(CategoryCreateDTO dto) {
        requireAdmin();
        String name = dto.getName().trim();
        Category existing = categoryMapper.selectByName(name);
        if (existing != null) {
            throw new BusinessException(HttpStatus.CONFLICT, "分类名称已存在");
        }

        Category category = CategoryConverter.toEntity(dto);
        category.setName(name);

        if (categoryMapper.insert(category) != 1) {
            throw new IllegalStateException("分类创建未影响预期记录数");
        }
    }

    @Override
    public void update(Long id, CategoryUpdateDTO dto) {
        requireAdmin();

        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "分类不存在");
        }

        String name = dto.getName().trim();

        Category existing = categoryMapper.selectByName(name);
        if (existing != null && !existing.getId().equals(id)) {
            throw new BusinessException(HttpStatus.CONFLICT, "分类名称已存在");
        }

        CategoryConverter.updateEntity(category, dto);
        category.setName(name);

        if (categoryMapper.updateById(category) != 1) {
            throw new IllegalStateException("分类更新未影响预期记录数");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        requireAdmin();

        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "分类不存在");
        }

        Long articleCount = categoryMapper.countArticlesByCategoryId(id);
        if (articleCount != null && articleCount > 0) {
            throw new BusinessException(HttpStatus.CONFLICT, "该分类下仍有文章，无法删除");
        }

        // 当前数据库没有外键级联，分类删除必须在同一事务中先清理知识关联。
        categoryTagService.deleteByCategoryId(id);
        int affectedRows = categoryMapper.deleteById(id);
        if (affectedRows == 0) {
            throw new IllegalStateException("分类删除未影响任何记录");
        }
    }

    @Override
    public List<CategoryVO> list() {
        // 普通分类列表不自动加载标签，避免为不需要关系数据的页面增加查询成本。
        List<Category> categories = categoryMapper.selectList();
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
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "未登录");
        }

        User user = userMapper.selectById(userId);
        PermissionUtil.checkAdmin(user);

        return user;
    }
}
