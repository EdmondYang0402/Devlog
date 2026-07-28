package com.myproject.devlog.mapper;

import com.myproject.devlog.pojo.entity.Category;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CategoryMapper {
    @Insert("INSERT INTO category(name, description, sort_order) VALUES (#{name}, #{description}, #{sortOrder})")
    void insert(Category category);

    @Select("SELECT * FROM category WHERE id = #{id}")
    Category getById(Long id);

    @Select("SELECT * FROM category WHERE name = #{name}")
    Category getByName(String name);

    @Select("SELECT * FROM category ORDER BY sort_order ASC, id ASC")
    List<Category> listAll();

    @Update("UPDATE category SET name = #{name}, description = #{description}, sort_order = #{sortOrder} WHERE id = #{id}")
    int update(Category category);

    @Delete("DELETE FROM category WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT COUNT(*) FROM article WHERE category_id = #{categoryId}")
    Long countArticlesByCategoryId(Long categoryId);

    @Select("SELECT COUNT(*) FROM category")
    Long countAll();
}
