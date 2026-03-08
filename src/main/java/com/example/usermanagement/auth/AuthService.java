package com.example.usermanagement.auth;

import com.example.usermanagement.auth.dto.AuthDtos;
import com.example.usermanagement.common.ApiResponse;
import com.example.usermanagement.user.User;
import com.example.usermanagement.user.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final AuthSessionRepository authSessionRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final PasswordRuleService passwordRuleService;
    private final NotificationGatewayClient notificationGatewayClient;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository,
                       AuthSessionRepository authSessionRepository,
                       PasswordResetTokenRepository passwordResetTokenRepository,
                       ApiKeyRepository apiKeyRepository,
                       PasswordRuleService passwordRuleService,
                       NotificationGatewayClient notificationGatewayClient) {
        this.userRepository = userRepository;
        this.authSessionRepository = authSessionRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.apiKeyRepository = apiKeyRepository;
        this.passwordRuleService = passwordRuleService;
        this.notificationGatewayClient = notificationGatewayClient;
    }

    public AuthDtos.RegisterResponse register(AuthDtos.RegisterRequest request) {
        PasswordValidationResult validation = passwordRuleService.validate(request.password());
        if (!validation.accepted()) {
            return new AuthDtos.RegisterResponse(null, "REJECTED", validation.code(), validation.message());
        }

        if (userRepository.existsByUsername(request.username())) {
            return new AuthDtos.RegisterResponse(null, "REJECTED", "DUPLICATE_USERNAME", "Username already exists");
        }
        if (userRepository.existsByEmail(request.email())) {
            return new AuthDtos.RegisterResponse(null, "REJECTED", "DUPLICATE_EMAIL", "Email already exists");
        }

        User user = new User(request.username(), request.email(), passwordEncoder.encode(request.password()));
        userRepository.save(user);
        return new AuthDtos.RegisterResponse(user.getId(), "ACCEPTED", "REGISTERED", "Account registered");
    }

    public AuthDtos.LoginResponse login(AuthDtos.LoginRequest request) {
        User user = userRepository.findByUsername(request.identifier())
                .or(() -> userRepository.findByEmail(request.identifier()))
                .orElse(null);
        if (user == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            return new AuthDtos.LoginResponse("REJECTED", "INVALID_CREDENTIALS", "Credentials rejected", null, false);
        }

        AuthSession session = new AuthSession(user, true, !user.isMfaEnabled());
        authSessionRepository.save(session);

        if (user.isMfaEnabled()) {
            return new AuthDtos.LoginResponse("ACCEPTED", "MFA_REQUIRED", "First-factor accepted; MFA required", session.getToken(), true);
        }
        return new AuthDtos.LoginResponse("ACCEPTED", "LOGIN_SUCCESS", "Authenticated", session.getToken(), false);
    }

    public AuthDtos.RefreshResponse refresh(String token) {
        AuthSession session = requireValidSession(token, true);
        User user = session.getUser();
        session.invalidate();
        authSessionRepository.save(session);

        AuthSession refreshed = new AuthSession(user, true, true);
        authSessionRepository.save(refreshed);

        return new AuthDtos.RefreshResponse("ACCEPTED", "SESSION_REFRESHED", "Session refreshed", refreshed.getToken());
    }

    public ApiResponse logout(String token) {
        AuthSession session = requireValidSession(token, false);
        session.invalidate();
        authSessionRepository.save(session);
        return ApiResponse.accepted("logout", "LOGOUT_SUCCESS", "Session invalidated");
    }

    public ApiResponse changePassword(String token, AuthDtos.ChangePasswordRequest request) {
        AuthSession session = requireValidSession(token, true);
        User user = session.getUser();

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            return ApiResponse.rejected("change-password", "INVALID_CURRENT_PASSWORD", "Current password is invalid");
        }

        PasswordValidationResult validation = passwordRuleService.validate(request.newPassword());
        if (!validation.accepted()) {
            return ApiResponse.rejected("change-password", validation.code(), validation.message());
        }

        user.updatePassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        return ApiResponse.accepted("change-password", "PASSWORD_UPDATED", "Password changed");
    }

    public AuthDtos.ResetRequestResponse resetRequest(AuthDtos.ResetRequest request) {
        User user = userRepository.findByUsername(request.identifier())
                .or(() -> userRepository.findByEmail(request.identifier()))
                .orElse(null);

        if (user == null) {
            return new AuthDtos.ResetRequestResponse("ACCEPTED", "RESET_REQUESTED", "If account exists, reset was initiated", null);
        }

        PasswordResetToken resetToken = new PasswordResetToken(user);
        passwordResetTokenRepository.save(resetToken);
        return new AuthDtos.ResetRequestResponse("ACCEPTED", "RESET_REQUESTED", "Reset requested", resetToken.getToken());
    }

    public ApiResponse resetConfirm(AuthDtos.ResetConfirmRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findById(request.resetToken()).orElse(null);
        if (resetToken == null || !resetToken.isUsable()) {
            return ApiResponse.rejected("reset-confirm", "INVALID_RESET_TOKEN", "Reset token invalid or expired");
        }

        PasswordValidationResult validation = passwordRuleService.validate(request.newPassword());
        if (!validation.accepted()) {
            return ApiResponse.rejected("reset-confirm", validation.code(), validation.message());
        }

        User user = resetToken.getUser();
        user.updatePassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        resetToken.markUsed();
        passwordResetTokenRepository.save(resetToken);

        return ApiResponse.accepted("reset-confirm", "PASSWORD_RESET", "Password reset complete");
    }

    public ApiResponse mfaChallenge(String token) {
        AuthSession session = requireValidSession(token, false);
        if (!session.isFirstFactorAuthenticated()) {
            return ApiResponse.rejected("mfa-challenge", "FIRST_FACTOR_REQUIRED", "First-factor authentication required");
        }

        String otp = session.generateOtp();
        boolean sent = notificationGatewayClient.sendOtp(session.getUser().getEmail(), otp);
        authSessionRepository.save(session);

        if (!sent) {
            return ApiResponse.rejected("mfa-challenge", "MFA_DELIVERY_FAILED", "OTP could not be delivered");
        }
        return ApiResponse.accepted("mfa-challenge", "MFA_CHALLENGE_SENT", "OTP challenge sent");
    }

    public ApiResponse mfaVerify(String token, AuthDtos.MfaVerifyRequest request) {
        AuthSession session = requireValidSession(token, false);
        if (session.verifyOtp(request.otp())) {
            authSessionRepository.save(session);
            return ApiResponse.accepted("mfa-verify", "MFA_VERIFIED", "MFA verification successful");
        }
        return ApiResponse.rejected("mfa-verify", "MFA_INVALID_CODE", "OTP invalid or expired");
    }

    public AuthDtos.ApiKeyIssueResponse issueApiKey(String token) {
        AuthSession session = requireValidSession(token, true);
        ApiKey apiKey = new ApiKey(session.getUser());
        apiKeyRepository.save(apiKey);
        return new AuthDtos.ApiKeyIssueResponse("ACCEPTED", "API_KEY_ISSUED", "API key issued", apiKey.getKeyId(), apiKey.getKeyValue());
    }

    public List<AuthDtos.ApiKeyView> listApiKeys(String token) {
        AuthSession session = requireValidSession(token, true);
        return apiKeyRepository.findByOwner(session.getUser()).stream()
                .map(k -> new AuthDtos.ApiKeyView(k.getKeyId(), k.getStatus(), k.getCreatedAt().toString()))
                .toList();
    }

    public ApiResponse revokeApiKey(String token, String keyId) {
        AuthSession session = requireValidSession(token, true);
        ApiKey apiKey = apiKeyRepository.findByKeyIdAndOwner(keyId, session.getUser()).orElse(null);
        if (apiKey == null) {
            return ApiResponse.rejected("api-key-revoke", "API_KEY_NOT_FOUND", "API key not found");
        }
        apiKey.revoke();
        apiKeyRepository.save(apiKey);
        return ApiResponse.accepted("api-key-revoke", "API_KEY_REVOKED", "API key revoked");
    }

    private AuthSession requireValidSession(String token, boolean fullyAuthenticated) {
        AuthSession session = authSessionRepository.findById(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid session token"));
        if (!session.isValid()) {
            throw new IllegalArgumentException("Invalid session token");
        }
        if (fullyAuthenticated && !session.isMfaVerified()) {
            throw new IllegalArgumentException("MFA verification required");
        }
        return session;
    }
}
