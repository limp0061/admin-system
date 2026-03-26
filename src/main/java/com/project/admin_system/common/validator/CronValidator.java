package com.project.admin_system.common.validator;

import com.project.admin_system.common.annotation.ValidCron;
import com.project.admin_system.common.utils.CronUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class CronValidator implements ConstraintValidator<ValidCron, String> {

    @Override
    public void initialize(ValidCron constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || value.isBlank()) {
            return false;
        }
        try {
            CronUtils.parse(value).validate();
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
