package com.myproject.devlog.mapper;

import com.myproject.devlog.pojo.entity.CategoryTag;
import com.myproject.devlog.pojo.entity.Tag;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

@Mapper
public interface CategoryTagMapper {
    @Select("SELECT tag_id FROM category_tag WHERE category_id = #{categoryId} ORDER BY tag_id")
    List<Long> selectTagIdsByCategoryId(Long categoryId);

    @Select("""
        SELECT t.id, t.name, t.create_time AS createTime, t.update_time AS updateTime
        FROM category_tag ct
        JOIN tag t ON t.id = ct.tag_id
        WHERE ct.category_id = #{categoryId}
        ORDER BY t.name ASC, t.id ASC
        """)
    List<Tag> selectTagsByCategoryId(Long categoryId);

    @Select("SELECT category_id FROM category_tag WHERE tag_id = #{tagId} ORDER BY category_id")
    List<Long> selectCategoryIdsByTagId(Long tagId);

    @Delete("DELETE FROM category_tag WHERE category_id = #{categoryId}")
    int deleteByCategoryId(Long categoryId);

    @Delete("DELETE FROM category_tag WHERE tag_id = #{tagId}")
    int deleteByTagId(Long tagId);

    @Insert("""
        <script>
        INSERT INTO category_tag(category_id, tag_id) VALUES
        <foreach collection="relations" item="relation" separator=",">
            (#{relation.categoryId}, #{relation.tagId})
        </foreach>
        </script>
        """)
    int insertBatch(@Param("relations") Collection<CategoryTag> relations);

    @Select("""
        SELECT COUNT(*) > 0 FROM category_tag
        WHERE category_id = #{categoryId} AND tag_id = #{tagId}
        """)
    boolean existsByCategoryIdAndTagId(@Param("categoryId") Long categoryId,
                                       @Param("tagId") Long tagId);

    @Delete("""
        DELETE FROM category_tag
        WHERE category_id = #{categoryId} AND tag_id = #{tagId}
        """)
    int deleteByCategoryIdAndTagId(@Param("categoryId") Long categoryId,
                                   @Param("tagId") Long tagId);
}
