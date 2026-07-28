package com.myproject.devlog.controller;

import com.myproject.devlog.common.PageResult;
import com.myproject.devlog.common.Result;
import com.myproject.devlog.pojo.dto.SiteBackgroundCreateDTO;
import com.myproject.devlog.pojo.dto.SiteBackgroundPageQueryDTO;
import com.myproject.devlog.pojo.dto.SiteBackgroundUpdateDTO;
import com.myproject.devlog.pojo.vo.SiteBackgroundAdminVO;
import com.myproject.devlog.service.SiteBackgroundService;
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
@RequestMapping("/admin/site-backgrounds")
public class AdminSiteBackgroundController {
    // 管理员身份沿用现有 /admin/** 拦截器链，上传仍由独立的现有上传接口负责。
    private final SiteBackgroundService service;

    public AdminSiteBackgroundController(SiteBackgroundService service) {
        this.service = service;
    }

    @GetMapping
    public Result<PageResult<SiteBackgroundAdminVO>> page(
            @ModelAttribute SiteBackgroundPageQueryDTO query) {
        return Result.success(service.adminPage(query));
    }

    @GetMapping("/{id}")
    public Result<SiteBackgroundAdminVO> detail(@PathVariable Long id) {
        return Result.success(service.getAdminDetail(id));
    }

    @PostMapping
    public Result<Long> create(@RequestBody SiteBackgroundCreateDTO dto) {
        return Result.success(service.create(dto));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SiteBackgroundUpdateDTO dto) {
        service.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return Result.success();
    }
}
