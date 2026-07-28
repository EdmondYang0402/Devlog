package com.myproject.devlog.controller;

import com.myproject.devlog.common.PageResult;
import com.myproject.devlog.common.Result;
import com.myproject.devlog.pojo.dto.ProjectCreateDTO;
import com.myproject.devlog.pojo.dto.ProjectPageQueryDTO;
import com.myproject.devlog.pojo.dto.ProjectUpdateDTO;
import com.myproject.devlog.pojo.vo.ProjectDetailVO;
import com.myproject.devlog.pojo.vo.ProjectListVO;
import com.myproject.devlog.service.ProjectService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/projects")
public class AdminProjectController {
    // 管理员身份继续由现有 /admin/** 拦截器链负责，Controller 不复制权限逻辑。
    private final ProjectService projectService;

    public AdminProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public Result<PageResult<ProjectListVO>> page(@ModelAttribute ProjectPageQueryDTO query) {
        return Result.success(projectService.adminPage(query));
    }

    @GetMapping("/{id}")
    public Result<ProjectDetailVO> detail(@PathVariable Long id) {
        return Result.success(projectService.getAdminDetail(id));
    }

    @PostMapping
    public Result<Long> create(@RequestBody ProjectCreateDTO dto) {
        return Result.success(projectService.create(dto));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ProjectUpdateDTO dto) {
        projectService.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        projectService.delete(id);
        return Result.success();
    }
}
