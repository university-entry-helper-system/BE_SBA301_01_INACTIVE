package org.example.sba.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.sba.command.RegisterCommand;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, RegisterCommand> {
    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
    }

    @Override
    public boolean isValid(RegisterCommand command, ConstraintValidatorContext context) {
        if (command.getPassword() == null || command.getConfirmPassword() == null) {
            return true; // Let @NotBlank handle null values
        }
        return command.getPassword().equals(command.getConfirmPassword());
    }
} 