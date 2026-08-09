package com.myproject.devlog.controller;

import com.myproject.devlog.common.Result;
import com.myproject.devlog.pojo.vo.SiteBackgroundPublicVO;
import com.myproject.devlog.service.SiteBackgroundService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/site-backgrounds")
public class SiteBackgroundController {
    private final SiteBackgroundService service;

    public SiteBackgroundController(SiteBackgroundService service) {
        this.service = service;
    }

    @GetMapping
    public Result<List<SiteBackgroundPublicVO>> enabledList() {
        return Result.success(service.listEnabled());
    }
}
