package com.example.usermanagement.auth;

public record PasswordValidationResult(boolean accepted, String code, String message) {
    public static PasswordValidationResult accepted() {
        return new PasswordValidationResult(true, "PASSWORD_RULE_ACCEPTED", "Password accepted");
    }

    public static PasswordValidationResult rejected(String code, String message) {
        return new PasswordValidationResult(false, code, message);
    }
}
