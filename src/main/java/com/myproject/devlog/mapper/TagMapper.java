package com.myproject.devlog.mapper;

import com.myproject.devlog.pojo.entity.Tag;
import com.myproject.devlog.pojo.vo.AdminTagVO;
import org.apache.ibatis.annotations.*;

import java.util.Collection;
import java.util.List;

@Mapper
public interface TagMapper {
    @Insert("INSERT INTO tag(name) VALUES (#{name})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Tag tag);

    @Update("UPDATE tag SET name = #{name} WHERE id = #{id}")
    int updateById(Tag tag);

    @Delete("DELETE FROM tag WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT id, name, create_time AS createTime, update_time AS updateTime FROM tag WHERE id = #{id}")
    Tag selectById(Long id);

    @Select("SELECT id, name, create_time AS createTime, update_time AS updateTime FROM tag WHERE name = #{name}")
    Tag selectByName(String name);

    @Select("SELECT COUNT(*) > 0 FROM tag WHERE name = #{name}")
    boolean existsByName(String name);

    @Select("SELECT COUNT(*) > 0 FROM tag WHERE name = #{name} AND id <> #{id}")
    boolean existsByNameExcludeId(@Param("name") String name, @Param("id") Long id);

    @Select("SELECT id, name, create_time AS createTime, update_time AS updateTime FROM tag ORDER BY name ASC, id ASC")
    List<Tag> selectList();

    @Select("""
        <script>
        SELECT id, name, create_time AS createTime, update_time AS updateTime
        FROM tag
        WHERE id IN
        <foreach collection="ids" item="id" open="(" separator="," close=")">#{id}</foreach>
        ORDER BY id
        </script>
        """)
    List<Tag> selectByIds(@Param("ids") Collection<Long> ids);

    @Select("""
        SELECT t.id, t.name, COUNT(at.article_id) AS articleCount,
               t.create_time AS createTime, t.update_time AS updateTime
        FROM tag t
        LEFT JOIN article_tag at ON at.tag_id = t.id
        GROUP BY t.id, t.name, t.create_time, t.update_time
        ORDER BY t.create_time DESC, t.id DESC
        """)
    List<AdminTagVO> selectAdminListWithArticleCount();

    @Select("SELECT COUNT(*) FROM article_tag WHERE tag_id = #{tagId}")
    long countArticleReferences(Long tagId);

    @Select("""
        <script>
        SELECT COUNT(*) FROM tag WHERE id IN
        <foreach collection="ids" item="id" open="(" separator="," close=")">#{id}</foreach>
        </script>
        """)
    long countByIds(@Param("ids") Collection<Long> ids);

    @Select("""
    SELECT t.id, t.name
    FROM tag t
    JOIN category_tag ct
      ON t.id = ct.tag_id
    WHERE ct.category_id = #{categoryId}
""")
    List<Tag> selectByCategoryId(Long categoryId);
}
