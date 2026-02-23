package com.project.admin_system.file.application.service;

import static com.project.admin_system.file.domain.utils.FilePathUtils.generateUniqueName;
import static com.project.admin_system.file.domain.utils.FilePathUtils.makeViewerUrl;

import com.project.admin_system.common.exception.BusinessException;
import com.project.admin_system.common.exception.ErrorCode;
import com.project.admin_system.common.service.S3StorageManager;
import com.project.admin_system.file.application.dto.FileResponse;
import com.project.admin_system.file.domain.DomainType;
import com.project.admin_system.file.domain.File;
import com.project.admin_system.file.domain.FileRepository;
import com.project.admin_system.file.domain.FileStatus;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {

    private final S3StorageManager storageManager;
    private final FileRepository fileRepository;

    public String fileUpload(MultipartFile file, DomainType domainType, Long userId) {

        String originalName = Objects.requireNonNull(file.getOriginalFilename());
        String savedFileName = generateUniqueName(originalName);
        String fullPath = domainType.resolvePath(userId, savedFileName);

        storageManager.upload(fullPath, file);

        return fullPath;
    }

    public String getFilePath(String filePath) {
        if (StringUtils.hasText(filePath)) {
            return storageManager.getUrl(filePath);
        }
        return null;
    }

    public void deleteFile(String profilePath) {
        storageManager.deleteFile(profilePath);
    }

    @Transactional
    public String uploadTemp(MultipartFile file, DomainType domainType) {

        String originalName = Objects.requireNonNull(file.getOriginalFilename());
        String savedFileName = generateUniqueName(originalName);
        String fullPath = DomainType.TEMP.resolvePath(null, savedFileName);

        storageManager.upload(fullPath, file);

        File temp = File.builder()
                .originalName(originalName)
                .storedFileName(savedFileName)
                .filePath(fullPath)
                .fileSize(file.getSize())
                .contentType(file.getContentType())
                .domainType(domainType)
                .domainId(null)
                .status(FileStatus.TEMP)
                .build();

        fileRepository.save(temp);

        return makeViewerUrl(temp.getId());
    }

    public FileResponse findFileById(Long id) {
        File file = fileRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.FILE_NOT_FOUND));
        return FileResponse.from(file);
    }

    public String getPresignedUrl(String filePath) {

        return storageManager.getPresignedUrl(filePath);
    }

    @Transactional
    public void finalizeImages(List<Long> fileIds, Long domainId, DomainType domainType) {
        if (fileIds == null || fileIds.isEmpty()) {
            return;
        }

        List<File> files = fileRepository.findAllByIdInAndStatus(fileIds, FileStatus.TEMP);
        for (File file : files) {
            String originalPath = file.getFilePath();
            String savedFileName = generateUniqueName(file.getOriginalName());
            String newPath = domainType.resolvePath(domainId, savedFileName);

            storageManager.moveObject(originalPath, newPath);

            file.finalizeFile(newPath, domainId);
        }
        List<File> existingFiles = fileRepository.findAllByDomainTypeAndDomainId(domainType, domainId);

        for (File existingFile : existingFiles) {
            if (!fileIds.contains(existingFile.getId())) {
                existingFile.markAsDeleted();
            }
        }
    }

    public void softDeleteFiles(List<Long> ids, DomainType domainType) {
        List<File> files = fileRepository.findByDomainIdInAndDomainType(ids, domainType);
        files.forEach(File::markAsDeleted);
    }
}
