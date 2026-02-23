package com.project.admin_system.notification.infrastructure;

import static com.project.admin_system.notification.domain.QNotificationMaster.notificationMaster;
import static com.project.admin_system.notification.domain.QNotificationRead.notificationRead;

import com.project.admin_system.notice.domain.Notice;
import com.project.admin_system.notification.application.dto.EventData;
import com.project.admin_system.notification.domain.NotificationMaster;
import com.project.admin_system.notification.domain.NotificationMasterRepositoryCustom;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationMasterRepositoryImpl implements NotificationMasterRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Long getUnReadCount(Long userId, LocalDateTime lastNoticeCheckAt) {
        Long count = queryFactory.select(notificationMaster.count())
                .from(notificationMaster)
                .leftJoin(notificationRead)
                .on(notificationRead.notification.eq(notificationMaster),
                        notificationRead.userId.eq(userId))
                .where(notificationRead.id.isNull(),
                        notificationMaster.createdAt.goe(lastNoticeCheckAt)
                ).fetchOne();

        return count != null ? count : 0L;
    }

    @Override
    public Long getUnReadCountOnlyForce(Long userId, LocalDateTime lastNoticeCheckAt) {
        Long count = queryFactory.select(notificationMaster.count())
                .from(notificationMaster)
                .leftJoin(notificationRead)
                .on(notificationRead.notification.eq(notificationMaster),
                        notificationRead.userId.eq(userId))
                .where(notificationRead.id.isNull(),
                        notificationMaster.createdAt.goe(lastNoticeCheckAt),
                        notificationMaster.isForce.isTrue()
                ).fetchOne();

        return count != null ? count : 0L;
    }

    @Override
    public List<EventData> findNotificationMasterTop10(Long userId, LocalDateTime lastNoticeCheckAt) {

        return queryFactory.select(
                        Projections.constructor(EventData.class,
                                notificationMaster.id,
                                notificationMaster.title,
                                notificationMaster.createdAt,
                                notificationMaster.url,
                                Expressions.constant(0L),
                                notificationRead.id.isNotNull()
                        )
                ).from(notificationMaster)
                .leftJoin(notificationRead)
                .on(notificationRead.notification.eq(notificationMaster),
                        notificationRead.userId.eq(userId))
                .where(notificationMaster.createdAt.goe(lastNoticeCheckAt)
                )
                .orderBy(notificationMaster.createdAt.desc())
                .limit(10)
                .fetch();
    }

    @Override
    public List<EventData> findNotificationMasterOnlyForceTop10(Long userId, LocalDateTime lastNoticeCheckAt) {

        return queryFactory.select(
                        Projections.constructor(EventData.class,
                                notificationMaster.id,
                                notificationMaster.title,
                                notificationMaster.createdAt,
                                notificationMaster.url,
                                Expressions.constant(0L),
                                notificationRead.id.isNotNull()
                        )
                ).from(notificationMaster)
                .leftJoin(notificationRead)
                .on(notificationRead.notification.eq(notificationMaster),
                        notificationRead.userId.eq(userId))
                .where(notificationMaster.createdAt.goe(lastNoticeCheckAt),
                        notificationMaster.isForce.isTrue()
                )
                .orderBy(notificationMaster.createdAt.desc())
                .limit(10)
                .fetch();
    }

    @Override
    public List<NotificationMaster> findAllUnreadByUserId(Long userId) {
        return queryFactory
                .selectFrom(notificationMaster)
                .leftJoin(notificationRead)
                .on(notificationRead.notification.eq(notificationMaster),
                        notificationRead.userId.eq(userId))
                .where(notificationRead.id.isNull()
                )
                .fetch();
    }
}
