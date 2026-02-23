package com.project.admin_system.user.application.validate;

import com.project.admin_system.common.exception.BusinessException;
import com.project.admin_system.common.exception.ErrorCode;

import com.project.admin_system.resources.domain.RoleRepository;
import com.project.admin_system.user.domain.UserRepository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Service
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public void validateDuplicateEmailId(String email) {

        if (userRepository.existsByEmailId(email)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL_ID);
        }
    }

    public List<Long> validateForDelete(List<Long> ids) {
        return userRepository.findAllReadyForDelete(ids);
    }

    public void validateDuplicateUserCode(String userCode) {
        if (StringUtils.hasText(userCode) && userRepository.existsByUserCode(userCode)) {
            throw new BusinessException(ErrorCode.DUPLICATE_USER_CODE);
        }
    }

    public static void validateIps(List<String> ips) {
        if (ips == null || ips.isEmpty()) {
            return;
        }

        for (String ip : ips) {
            if (!isValidIpv4OrCidr(ip)) {
                throw new BusinessException(ErrorCode.INVALID_IP_FORMAT);
            }
        }
    }

    public static boolean isValidIpv4OrCidr(String input) {

        if (input == null || input.isBlank()) {
            return true;
        }

        String[] parts = input.split("/");

        if (!InetAddressValidator.getInstance()
                .isValidInet4Address(parts[0])) {
            return false;
        }

        if (parts.length == 1) {
            return true;
        }

        if (parts.length == 2) {
            try {
                int prefix = Integer.parseInt(parts[1]);
                return prefix >= 0 && prefix <= 32;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    public void validateAdminRole(Long id) {
        if (userRepository.existsAdminRole(id)) {
            throw new BusinessException(ErrorCode.DUPLICATE_ROLE_ASSIGNMENT);
        }
    }

}
