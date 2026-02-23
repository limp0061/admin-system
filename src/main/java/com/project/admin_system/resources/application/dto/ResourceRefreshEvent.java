package com.project.admin_system.resources.application.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record ResourceRefreshEvent(
        String targetType,
        List<Long> targetIds,
        String action
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}
