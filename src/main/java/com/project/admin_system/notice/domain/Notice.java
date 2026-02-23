package com.project.admin_system.notice.domain;

import com.project.admin_system.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.sql.Types;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;

@Getter
@Entity
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "notice_type", nullable = false)
    private NoticeType type;

    @Column(length = 100, nullable = false)
    private String title;

    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean isRealtimeNotified;

    @Column(nullable = false)
    private boolean isForce;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    protected Notice() {
    }

    @Builder
    private Notice(Long id, NoticeType type, String title, String content, boolean isRealtimeNotified,
                   boolean isForce, LocalDateTime startAt, LocalDateTime endAt) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.content = content;
        this.isRealtimeNotified = isRealtimeNotified;
        this.isForce = isForce;
        this.startAt = startAt;
        this.endAt = endAt;
    }
}
