package com.project.admin_system.statistic.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    @GetMapping("/login")
    public String loginStatistics() {
        return "page/statistics/login";
    }

    @GetMapping("/system")
    public String systemStatistics() {
        return "page/statistics/system";
    }
}
