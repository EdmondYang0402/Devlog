package com.myproject.devlog.service.impl;

import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.mapper.TagMapper;
import com.myproject.devlog.pojo.dto.TagCreateDTO;
import com.myproject.devlog.pojo.dto.TagUpdateDTO;
import com.myproject.devlog.pojo.entity.Tag;
import com.myproject.devlog.pojo.vo.AdminTagVO;
import com.myproject.devlog.pojo.vo.TagVO;
import com.myproject.devlog.service.TagService;
import com.myproject.devlog.utils.TagConverter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    private final TagMapper tagMapper;

    public TagServiceImpl(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    @Override
    public void create(TagCreateDTO dto) {
        String name = dto.getName();

        if (name == null) {
            throw new BusinessException("标签名称不能为空");
        }

        name = name.trim();

        if (name.isEmpty()) {
            throw new BusinessException("标签名称不能为空");
        }

        if (name.length() > 50) {
            throw new BusinessException("标签名称不能超过50个字符");
        }

        if (tagMapper.existsByName(name)) {
            throw new BusinessException("标签已存在");
        }

        Tag tag = TagConverter.fromCreateDTO(dto);
        tag.setName(name);

        tagMapper.insert(tag);
    }

    @Override
    public void update(Long id, TagUpdateDTO dto) {
        Tag tag = tagMapper.selectById(id);

        if (tag == null) {
            throw new BusinessException("标签不存在");
        }

        String name = dto.getName();

        if (name == null) {
            throw new BusinessException("标签名称不能为空");
        }

        name = name.trim();

        if (name.isEmpty()) {
            throw new BusinessException("标签名称不能为空");
        }

        if (name.length() > 50) {
            throw new BusinessException("标签名称不能超过50个字符");
        }

        if (tagMapper.existsByNameExcludeId(name, id)) {
            throw new BusinessException("标签名称已存在");
        }

        tag.setName(name);
        tagMapper.update(tag);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Tag tag = tagMapper.selectById(id);
        if (tag == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "标签不存在");
        }
        if (tagMapper.countArticleReferences(id) > 0) {
            throw new BusinessException(HttpStatus.CONFLICT, "该标签仍被文章引用，无法删除");
        }
        if (tagMapper.deleteById(id) == 0) {
            throw new BusinessException("标签删除失败");
        }
    }

    @Override
    public List<TagVO> listAll() {
        return tagMapper.listAll()
                .stream()
                .map(TagConverter::toVO)
                .toList();
    }

    @Override
    public List<AdminTagVO> adminList() {
        return tagMapper.adminListWithArticleCount();
    }
}
