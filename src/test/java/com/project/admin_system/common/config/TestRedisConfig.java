package com.project.admin_system.common.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;

@Slf4j
@TestConfiguration
public class TestRedisConfig {

    private redis.embedded.RedisServer redisServer;

    @PostConstruct
    public void startRedis() {
        try {
            redisServer = redis.embedded.RedisServer.builder()
                    .port(6379)
                    .setting("maxmemory 128mb")
                    .build();
            redisServer.start();
        } catch (Exception e) {
            // 이미 실행 중이면 무시
            log.info("Redis already running: " + e.getMessage());
        }
    }

    @PreDestroy
    public void stopRedis() {
        try {
            if (redisServer != null && redisServer.isActive()) {
                redisServer.stop();
            }
        } catch (Exception e) {
            log.info("Redis stop error: " + e.getMessage());
        }
    }
}