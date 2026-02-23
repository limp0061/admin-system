package com.project.admin_system.userdept.domain;


import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDeptRepository extends JpaRepository<UserDept, Long>, UserDeptRepositoryCustom {

}
