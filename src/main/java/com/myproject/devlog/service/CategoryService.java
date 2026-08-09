package com.myproject.devlog.service;

import com.myproject.devlog.pojo.dto.CategoryCreateDTO;
import com.myproject.devlog.pojo.dto.CategoryUpdateDTO;
import com.myproject.devlog.pojo.vo.CategoryVO;
import java.util.List;

public interface CategoryService {
    void create(CategoryCreateDTO dto);
    void update(Long id, CategoryUpdateDTO dto);
    void delete(Long id);
    List<CategoryVO> list();
}
