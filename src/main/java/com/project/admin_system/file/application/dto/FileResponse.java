package com.project.admin_system.file.application.dto;

import com.project.admin_system.file.domain.File;

public record FileResponse(
        String filePath,
        String contentType,
        String originalName
) {

    public static FileResponse from(File file) {
        return new FileResponse(
                file.getFilePath(),
                file.getContentType(),
                file.getOriginalName()
        );
    }
}
