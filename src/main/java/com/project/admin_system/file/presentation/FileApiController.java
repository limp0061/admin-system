package com.project.admin_system.file.presentation;


import com.project.admin_system.file.application.dto.FileResponse;
import com.project.admin_system.file.application.service.FileService;

import com.project.admin_system.file.domain.DomainType;
import java.net.URI;
import java.util.Map;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileApiController {

    private final FileService fileService;

    @PostMapping("/editor-upload")
    public ResponseEntity<Map<String, String>> upload(
            @RequestPart(name = "upload") MultipartFile file,
            @RequestParam(name = "type") DomainType domainType
    ) {
        String uploadUrl = fileService.uploadTemp(file, domainType);
        Map<String, String> data = Map.of("url", uploadUrl);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/viewer/{id}")
    public ResponseEntity<Void> viewImage(
            @PathVariable(name = "id") Long id
    ) {
        FileResponse fileResponse = fileService.findFileById(id);

        String presignedUrl = fileService.getPresignedUrl(fileResponse.filePath());

        return ResponseEntity.status(HttpStatus.FOUND) // 302
                .location(URI.create(presignedUrl))
                .build();
    }
}
