package com.project.admin_system.common.exception;


import org.springframework.security.core.AuthenticationException;

public class IpAddressRejectedException extends AuthenticationException {
    public IpAddressRejectedException(String message) {
        super(message);
    }
}
