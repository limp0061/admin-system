package com.project.admin_system.file.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long>, FileRepositoryCustom {
    List<File> findAllByIdInAndStatus(List<Long> ids, FileStatus status);

    List<File> findAllByDomainTypeAndDomainId(DomainType domainType, Long domainId);

    List<File> findByDomainIdInAndDomainType(List<Long> ids, DomainType domainType);
}
