package com.project.admin_system.resources.domain;

import com.project.admin_system.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Table(name = "roles")
public class Role extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @Column(length = 50, unique = true, nullable = false)
    private String roleKey;

    @Column(length = 100, nullable = false)
    private String roleName;

    private int depth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Role parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private Set<Role> children = new HashSet<>();

    @Column(nullable = false)
    private boolean isAdmin;

    protected Role() {
    }

    @Builder
    public Role(String roleKey, String roleName, int depth, Role parent, boolean isAdmin) {
        this.roleKey = roleKey;
        this.roleName = roleName;
        this.depth = depth;
        this.parent = parent;
        this.isAdmin = isAdmin;
    }

    public void update(String roleKey, String roleName, Role parent, boolean isAdmin) {
        this.roleKey = roleKey;
        this.roleName = roleName;
        this.depth = parent != null ? parent.getDepth() + 1 : 0;
        this.parent = parent;
        this.isAdmin = isAdmin;
    }
}
