package com.project.admin_system.userdept.domain;

import com.project.admin_system.dept.domain.Dept;
import com.project.admin_system.user.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
public class UserDept {

    @Id
    private Long userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id")
    private Dept dept;

    protected UserDept() {
    }

    @Builder
    public UserDept(User user, Dept dept) {
        this.user = user;
        this.dept = dept;
        if (user != null) {
            this.userId = user.getId(); // PK 공유 설정
            assignUser(user);           // 연관관계 편의 메서드
        }
    }

    public void assignUser(User user) {
        this.user = user;
        if (user != null && user.getUserDept() != this) {
            user.assignDepartment(this);
        }
    }

    public void updateDept(Dept dept) {
        this.dept = dept;
    }
}
