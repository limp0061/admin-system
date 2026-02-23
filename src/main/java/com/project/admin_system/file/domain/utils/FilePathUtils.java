package com.project.admin_system.file.domain.utils;

import java.util.UUID;

public class FilePathUtils {

    public static final String IMAGE_VIEWER = "/api/v1/files/viewer/";

    public static String generateUniqueName(String originalFileName) {
        String uuid = UUID.randomUUID().toString().replace("-", "");

        int dotIndex = originalFileName.lastIndexOf(".");
        String ext = (dotIndex == -1) ? "" : originalFileName.substring(dotIndex);
        return uuid + ext;
    }

    public static String makeViewerUrl(Long fileId) {
        return IMAGE_VIEWER + fileId;
    }
}
