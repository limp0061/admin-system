package com.project.admin_system.common.dto;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Order;

public record PageResponse<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalCount,
        int totalPages,
        boolean isLastPage,

        String sortBy,
        String direction

) {
    public static <T> PageResponse<T> of(Page<T> page) {

        String sortBy = "updatedAt";
        String direction = "DESC";

        if (page.getSort().isSorted()) {
            Order order = page.getSort().iterator().next();
            sortBy = order.getProperty();
            direction = order.getDirection().name();
        }

        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast(),
                sortBy,
                direction
        );
    }
}


