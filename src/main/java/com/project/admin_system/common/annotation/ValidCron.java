package com.project.admin_system.common.annotation;


import com.project.admin_system.common.validator.CronValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CronValidator.class)
@NotBlank(message = "실행 주기를 입력해주세요.")
public @interface ValidCron {
    String message() default "올바르지 않은 Cron 표현식입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
