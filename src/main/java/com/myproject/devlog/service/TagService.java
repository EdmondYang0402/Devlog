package com.myproject.devlog.service;

import com.myproject.devlog.pojo.dto.TagCreateDTO;
import com.myproject.devlog.pojo.dto.TagUpdateDTO;
import com.myproject.devlog.pojo.vo.AdminTagVO;
import com.myproject.devlog.pojo.vo.TagVO;

import java.util.List;

public interface TagService {
    void create(TagCreateDTO dto);
    void update(Long id, TagUpdateDTO dto);
    void delete(Long id);
    List<TagVO> listAll();
    List<AdminTagVO> adminList();
}
