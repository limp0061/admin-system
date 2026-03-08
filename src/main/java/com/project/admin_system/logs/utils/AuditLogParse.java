package com.project.admin_system.logs.utils;

import static com.project.admin_system.logs.constants.AuditColumnLabels.getMapByTargetEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.admin_system.common.exception.BusinessException;
import com.project.admin_system.common.exception.ErrorCode;
import com.project.admin_system.logs.domain.AuditTarget;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditLogParse {

    private final ObjectMapper objectMapper;

    public Map<String, Object> parse(String detail, AuditTarget target) {
        Map<String, Object> rawMap = parseDetails(detail);
        Map<String, String> labels = getMapByTargetEntity(target);

        Map<String, Object> result = new LinkedHashMap<>();
        for (Entry<String, Object> entry : rawMap.entrySet()) {
            if (labels.containsKey(entry.getKey())) {
                Object value = entry.getValue();
                String key = labels.get(entry.getKey());
                result.put(key, value);
            }
        }

        return result;
    }

    private Map<String, Object> parseDetails(String detail) {
        if (detail == null || detail.isBlank()) {
            return Map.of();
        }
        
        try {
            return objectMapper.readValue(detail, Map.class);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.INVALID_LOG_FORMAT);
        }
    }
}
