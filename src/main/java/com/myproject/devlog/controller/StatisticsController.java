package com.myproject.devlog.controller;

import com.myproject.devlog.common.Result;
import com.myproject.devlog.pojo.vo.StatisticsVO;
import com.myproject.devlog.service.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/profile")
    public Result<StatisticsVO> getProfileStatistics() {
        return Result.success(statisticsService.getProfileStatistics());
    }
}
