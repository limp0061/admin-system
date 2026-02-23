package com.project.admin_system.common.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisManager {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void setData(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public <T> T getData(String key, Class<T> classz) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        return objectMapper.convertValue(value, classz);
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }

    public void hasKey(String key) {
        redisTemplate.hasKey(key);
    }

    public void deleteDataByPrefix(String prefix) {
        Set<String> keys = redisTemplate.keys(prefix + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
