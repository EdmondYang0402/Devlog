package com.myproject.devlog.utils;

import com.myproject.devlog.pojo.dto.TagCreateDTO;
import com.myproject.devlog.pojo.entity.Tag;
import com.myproject.devlog.pojo.vo.AdminTagVO;
import com.myproject.devlog.pojo.vo.TagVO;
import org.springframework.beans.BeanUtils;

public final class TagConverter {
    private TagConverter() {
    }

    public static Tag fromCreateDTO(TagCreateDTO dto) {
        if (dto == null) return null;
        Tag tag = new Tag();
        BeanUtils.copyProperties(dto, tag);
        return tag;
    }

    public static TagVO toVO(Tag tag) {
        if (tag == null) return null;
        TagVO vo = new TagVO();
        BeanUtils.copyProperties(tag, vo);
        return vo;
    }

    public static TagVO toVO(Long id, String name) {
        TagVO vo = new TagVO();
        vo.setId(id);
        vo.setName(name);
        return vo;
    }

    public static AdminTagVO toAdminVO(Tag tag) {
        if (tag == null) return null;
        AdminTagVO vo = new AdminTagVO();
        BeanUtils.copyProperties(tag, vo);
        return vo;
    }
}
