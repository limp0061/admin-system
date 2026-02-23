package com.project.admin_system.file.domain;

import com.project.admin_system.common.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Builder;
import lombok.Getter;


@Getter
@Entity
public class File extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    Long id;

    @Column(length = 100, nullable = false)
    String originalName;

    @Column(length = 100, nullable = false)
    String storedFileName;

    @Column(nullable = false)
    String filePath;

    Long fileSize;

    @Column(length = 50, nullable = false)
    String contentType;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    DomainType domainType;

    Long domainId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    FileStatus status;

    protected File() {
    }

    @Builder
    public File(
            String originalName,
            String storedFileName,
            String filePath,
            Long fileSize,
            String contentType,
            DomainType domainType,
            Long domainId,
            FileStatus status
    ) {
        this.originalName = originalName;
        this.storedFileName = storedFileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.domainType = domainType;
        this.domainId = domainId;
        this.status = status;
    }

    public void finalizeFile(String newPath, Long domainId) {
        this.filePath = newPath;
        this.domainId = domainId;
        this.status = FileStatus.STORAGE;
    }

    public void markAsDeleted() {
        this.status = FileStatus.DELETED;
        this.delete();
    }
}
