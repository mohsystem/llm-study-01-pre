package com.example.usermanagement.auth;

import com.example.usermanagement.auth.dto.AuthDtos;
import com.example.usermanagement.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDtos.RegisterResponse> register(@RequestBody AuthDtos.RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDtos.LoginResponse> login(@RequestBody AuthDtos.LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthDtos.RefreshResponse> refresh(@RequestHeader("X-Session-Token") String token) {
        return ResponseEntity.ok(authService.refresh(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@RequestHeader("X-Session-Token") String token) {
        return ResponseEntity.ok(authService.logout(token));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(
            @RequestHeader("X-Session-Token") String token,
            @RequestBody AuthDtos.ChangePasswordRequest request
    ) {
        return ResponseEntity.ok(authService.changePassword(token, request));
    }

    @PostMapping("/reset-request")
    public ResponseEntity<AuthDtos.ResetRequestResponse> resetRequest(@RequestBody AuthDtos.ResetRequest request) {
        return ResponseEntity.ok(authService.resetRequest(request));
    }

    @PostMapping("/reset-confirm")
    public ResponseEntity<ApiResponse> resetConfirm(@RequestBody AuthDtos.ResetConfirmRequest request) {
        return ResponseEntity.ok(authService.resetConfirm(request));
    }

    @PostMapping("/mfa/challenge")
    public ResponseEntity<ApiResponse> mfaChallenge(@RequestHeader("X-Session-Token") String token) {
        return ResponseEntity.ok(authService.mfaChallenge(token));
    }

    @PostMapping("/mfa/verify")
    public ResponseEntity<ApiResponse> mfaVerify(
            @RequestHeader("X-Session-Token") String token,
            @RequestBody AuthDtos.MfaVerifyRequest request
    ) {
        return ResponseEntity.ok(authService.mfaVerify(token, request));
    }

    @PostMapping("/api-keys")
    public ResponseEntity<AuthDtos.ApiKeyIssueResponse> issueApiKey(@RequestHeader("X-Session-Token") String token) {
        return ResponseEntity.ok(authService.issueApiKey(token));
    }

    @GetMapping("/api-keys")
    public ResponseEntity<List<AuthDtos.ApiKeyView>> listApiKeys(@RequestHeader("X-Session-Token") String token) {
        return ResponseEntity.ok(authService.listApiKeys(token));
    }

    @DeleteMapping("/api-keys/{keyId}")
    public ResponseEntity<ApiResponse> revokeApiKey(
            @RequestHeader("X-Session-Token") String token,
            @PathVariable String keyId
    ) {
        return ResponseEntity.ok(authService.revokeApiKey(token, keyId));
    }
}
