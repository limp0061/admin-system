package com.project.admin_system.resources.application.validate;

import com.project.admin_system.common.exception.BusinessException;
import com.project.admin_system.common.exception.ErrorCode;
import com.project.admin_system.resources.application.dto.ResourcesSaveRequest;
import com.project.admin_system.resources.domain.ResourcesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourcesValidator {

    private final ResourcesRepository resourcesRepository;

    public void checkDuplicate(ResourcesSaveRequest request) {
        String urlPattern = request.urlPattern().trim().toLowerCase();
        if (!urlPattern.startsWith("/")) {
            urlPattern = "/" + urlPattern;
        }

        if (resourcesRepository.existsByUrlPatternAndMethod(urlPattern, request.method())) {
            throw new BusinessException(ErrorCode.DUPLICATE_URL_PATTERN);
        }
    }

    public void checkDuplicateForUpdate(ResourcesSaveRequest request) {
        String urlPattern = request.urlPattern().trim().toLowerCase();
        if (!urlPattern.startsWith("/")) {
            urlPattern = "/" + urlPattern;
        }

        if (resourcesRepository.existsByUrlPatternAndMethodAndIdNot(urlPattern, request.method(), request.id())) {
            throw new BusinessException(ErrorCode.DUPLICATE_URL_PATTERN);
        }
    }
}
