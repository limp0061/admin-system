package com.project.admin_system.user.domain;

import static jakarta.persistence.FetchType.LAZY;

import com.project.admin_system.common.domain.BaseEntity;
import com.project.admin_system.dept.domain.Dept;
import com.project.admin_system.resources.domain.Role;
import com.project.admin_system.user.application.dto.UserUpdateRequest;
import com.project.admin_system.userdept.domain.UserDept;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(length = 100, nullable = false)
    private String emailId;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 100, nullable = false)
    private String password;

    @Column(length = 20)
    private String position;

    @Column(length = 20)
    private String userCode;

    private int passwordFailCount;

    private String profilePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_allowed_ips",
            joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "ip_address", length = 50)
    private Set<String> allowedIps = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = LAZY, orphanRemoval = true)
    private UserDept userDept;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private UserStatus userStatus;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = LAZY, orphanRemoval = true)
    private UserConfig userConfig;

    private LocalDateTime lastLoginTime;

    protected User() {
    }

    @Builder
    private User(String emailId, String name, String password, String position, String userCode, Gender gender,
                 UserStatus userStatus, String profilePath, Role role) {
        this.emailId = emailId;
        this.name = name;
        this.password = password;
        this.position = position;
        this.userCode = userCode;
        this.gender = gender;
        this.userStatus = userStatus != null ? userStatus : UserStatus.ACTIVE;
        this.passwordFailCount = 0;
        this.profilePath = profilePath;
        this.role = role;
    }

    public void assignDepartment(UserDept userDept) {
        this.userDept = userDept;
        if (userDept != null && userDept.getUser() != this) {
            userDept.assignUser(this);
        }
    }

    public void update(UserUpdateRequest dto, Dept newDept, Role role) {
        if (dto.name() != null && !dto.name().isBlank()) {
            this.name = dto.name();
        }

        if (dto.emailId() != null && !dto.emailId().isBlank()) {
            this.emailId = dto.emailId();
        }

        if (dto.password() != null && !dto.password().isBlank()) {
            this.password = dto.password();
        }

        this.position = dto.position();
        this.userCode = dto.userCode();

        if (dto.gender() != null) {
            this.gender = dto.gender();
        }

        if (dto.userStatus() != null) {
            updateUserStatus(dto.userStatus());
        }

        if (role != null) {
            this.role = role;
        }

        updateUserDept(newDept);
    }

    private void updateUserDept(Dept newDept) {
        if (newDept == null) {
            if (this.userDept != null) {
                this.userDept = null;
            }
            return;
        }

        if (this.userDept == null) {
            assignDepartment(new UserDept(this, newDept));
        } else {
            this.userDept.updateDept(newDept);
        }
    }

    public void updateUserStatus(UserStatus newStatus) {
        if (newStatus != null) {
            if (newStatus == UserStatus.ACTIVE) {
                unLocked();
            } else if (newStatus == UserStatus.DELETED) {
                this.userStatus = UserStatus.DELETED;
                this.delete();
            } else {
                this.userStatus = newStatus;
                this.activate();
            }
        }
    }

    private void unLocked() {
        this.userStatus = UserStatus.ACTIVE;
        this.passwordFailCount = 0;
    }

    public boolean isManagedUser() {
        return !isDeleted()
                && this.userStatus != UserStatus.DELETED
                && this.userStatus != UserStatus.INACTIVE;
    }

    public void initDefaultConfig() {
        this.userConfig = UserConfig.builder()
                .user(this)
                .isReceivedNotice(true)
                .lastNoticeCheckAt(LocalDateTime.now())
                .build();
    }

    public void updateProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    public void addRole(Role role) {
        this.role = role;
    }

    public void addIps(List<String> newIps) {
        if (newIps != null && !newIps.isEmpty()) {
            this.allowedIps.clear();
            this.allowedIps.addAll(newIps);
        }
    }

    public void loginSuccess() {
        this.lastLoginTime = LocalDateTime.now();
        this.passwordFailCount = 0;
    }

    public void loginFailure() {
        if (this.userStatus == UserStatus.LOCKED) {
            return;
        }

        this.passwordFailCount += 1;
        if (this.passwordFailCount >= 5) {
            this.userStatus = UserStatus.LOCKED;
        }
    }

    public void assignRole(Role defaultRole) {
        this.role = defaultRole;
    }

    public void resetToDefaultRole(Role defaultRole) {
        assignRole(defaultRole);
        this.allowedIps.clear();
    }

    public void encPassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
