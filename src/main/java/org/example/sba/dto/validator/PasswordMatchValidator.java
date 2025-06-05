package org.example.sba.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.sba.dto.request.ChangePasswordDTO;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, ChangePasswordDTO> {
    @Override
    public boolean isValid(ChangePasswordDTO value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return value.getNewPassword() != null && value.getNewPassword().equals(value.getConfirmPassword());
    }
} 