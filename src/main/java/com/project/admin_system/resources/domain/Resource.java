package com.project.admin_system.resources.domain;

import com.project.admin_system.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
public class Resource extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resource_id")
    private Long id;

    @Column(length = 100, name = "resource_name")
    private String name;

    @Column(nullable = false)
    private String urlPattern;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Method method;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "resource_roles",
            joinColumns = @JoinColumn(name = "resource_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    private String description;

    protected Resource() {
    }

    @Builder
    public Resource(String name, String urlPattern, Method method, String description) {
        this.name = name;
        this.urlPattern = urlPattern;
        this.method = method;
        this.description = description;
    }

    @PrePersist
    @PreUpdate
    public void formatUrl() {
        if (this.urlPattern != null) {
            this.urlPattern = this.urlPattern.toLowerCase();
            if (!this.urlPattern.startsWith("/")) {
                this.urlPattern = "/" + this.urlPattern;
            }
        }
    }

    public void updateFields(String name, Method method, String description) {
        this.name = name;
        this.method = method;
        this.description = description;
    }

    public void addRoles(List<Role> newRoles) {
        this.roles.clear();
        if (newRoles != null && !newRoles.isEmpty()) {
            this.roles.addAll(newRoles);
        }
    }
}
