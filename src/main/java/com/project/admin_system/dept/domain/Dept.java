package com.project.admin_system.dept.domain;

import com.project.admin_system.common.domain.BaseEntity;
import com.project.admin_system.dept.application.dto.DeptSaveRequest;
import com.project.admin_system.userdept.domain.UserDept;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
public class Dept extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dept_id")
    private Long id;

    @Column(length = 50, unique = true, nullable = false)
    private String deptCode;

    @Column(length = 50, nullable = false)
    private String deptName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "upper_dept_id")
    private Dept upperDept;

    @OneToMany(mappedBy = "upperDept", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Dept> children = new ArrayList<>();

    private boolean isActive;
    private int sortOrder;

    private int depth;

    @OneToMany(mappedBy = "dept", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<UserDept> userDepts = new ArrayList<>();

    protected Dept() {
    }

    @Builder
    private Dept(String deptCode, String deptName, Dept upperDept,
                 Integer sortOrder, Boolean isActive, int depth) {
        this.deptCode = deptCode;
        this.deptName = deptName;
        this.upperDept = upperDept;
        this.sortOrder = (sortOrder != null) ? sortOrder : 0;
        this.isActive = (isActive != null) ? isActive : true;
        this.depth = depth;
    }

    public void update(DeptSaveRequest request, Dept parentDept, int calculatedDepth) {

        this.deptCode = request.deptCode();
        this.deptName = request.deptName();
        this.upperDept = parentDept;
        this.depth = calculatedDepth;

        this.sortOrder = (request.sortOrder() != null) ? request.sortOrder() : 0;
        this.isActive = (request.isActive() != null) ? request.isActive() : true;
    }

    public void updateParent(Dept parent) {
        this.upperDept = parent;
    }
}


