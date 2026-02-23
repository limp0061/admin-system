package com.project.admin_system.notification.domain;

import com.project.admin_system.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
public class NotificationMaster extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    private String url;

    private Long noticeId;

    @Column(nullable = false)
    private boolean isForce;

    @OneToMany(mappedBy = "notification")
    private List<NotificationRead> notificationReads = new ArrayList<>();

    protected NotificationMaster() {
    }

    @Builder
    private NotificationMaster(String title, String url, Long noticeId, boolean isForce) {
        this.title = title;
        this.url = url;
        this.noticeId = noticeId;
        this.isForce = isForce;
    }
}
