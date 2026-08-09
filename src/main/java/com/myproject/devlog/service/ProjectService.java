package com.myproject.devlog.service;

import com.myproject.devlog.common.PageResult;
import com.myproject.devlog.pojo.dto.ProjectCreateDTO;
import com.myproject.devlog.pojo.dto.ProjectPageQueryDTO;
import com.myproject.devlog.pojo.dto.ProjectUpdateDTO;
import com.myproject.devlog.pojo.vo.ProjectDetailVO;
import com.myproject.devlog.pojo.vo.ProjectListVO;

import java.util.List;

public interface ProjectService {
    Long create(ProjectCreateDTO dto);

    void update(Long id, ProjectUpdateDTO dto);

    void delete(Long id);

    ProjectDetailVO getAdminById(Long id);

    PageResult<ProjectListVO> pageAdmin(ProjectPageQueryDTO query);

    ProjectDetailVO getById(Long id);

    PageResult<ProjectListVO> page(ProjectPageQueryDTO query);

    List<ProjectListVO> listFeatured(Integer limit);
}
