package com.project.admin_system.common.utils;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;

import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;
import org.springframework.data.domain.Sort;

public class QueryDslUtil {

    public static OrderSpecifier<?>[] getOrderSpecifier(Sort sort, EntityPathBase<?> entityPath) {
        PathBuilder<?> pathBuilder = new PathBuilder<>(entityPath.getType(), entityPath.getMetadata());

        return sort.stream()
                .map(order -> {
                    Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                    String cond = order.getProperty();
                    return new OrderSpecifier(direction, pathBuilder.get(cond));
                })
                .toArray(OrderSpecifier[]::new);
    }
}
