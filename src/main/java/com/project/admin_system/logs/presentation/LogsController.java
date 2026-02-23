package com.project.admin_system.logs.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/logs")
@RequiredArgsConstructor
public class LogsController {

    @GetMapping("/user")
    public String userLogs() {
        return "/page/logs/user";
    }

    @GetMapping("/admin")
    public String adminLogs() {
        return "/page/logs/admin";
    }
}
