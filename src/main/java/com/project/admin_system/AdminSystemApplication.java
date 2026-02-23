package com.project.admin_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class AdminSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminSystemApplication.class, args);
    }
}
