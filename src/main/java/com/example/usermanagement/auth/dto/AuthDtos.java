package com.example.usermanagement.auth.dto;

import com.example.usermanagement.auth.PasswordRuleConfig;

public class AuthDtos {
    public record RegisterRequest(String username, String email, String password) {}
    public record RegisterResponse(Long accountId, String status, String code, String message) {}

    public record LoginRequest(String identifier, String password) {}
    public record LoginResponse(String status, String code, String message, String token, boolean mfaRequired) {}

    public record RefreshResponse(String status, String code, String message, String token) {}

    public record ChangePasswordRequest(String currentPassword, String newPassword) {}

    public record ResetRequest(String identifier) {}
    public record ResetRequestResponse(String status, String code, String message, String resetToken) {}

    public record ResetConfirmRequest(String resetToken, String newPassword) {}

    public record MfaChallengeResponse(String status, String code, String message) {}
    public record MfaVerifyRequest(String otp) {}

    public record ApiKeyIssueResponse(String status, String code, String message, String keyId, String keyValue) {}
    public record ApiKeyView(String keyId, String status, String createdAt) {}

    public record PasswordRuleRequest(int minLength, boolean requireUppercase, boolean requireLowercase, boolean requireDigit, boolean requireSpecial) {
        public PasswordRuleConfig toConfig() {
            return new PasswordRuleConfig(1L, minLength, requireUppercase, requireLowercase, requireDigit, requireSpecial);
        }
    }
}
