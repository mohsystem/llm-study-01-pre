package com.example.usermanagement.auth;

import com.example.usermanagement.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "auth_sessions")
public class AuthSession {
    @Id
    private String token;

    @ManyToOne(optional = false)
    private User user;

    private boolean active;
    private boolean firstFactorAuthenticated;
    private boolean mfaVerified;
    private String pendingOtp;
    private LocalDateTime otpExpiresAt;
    private LocalDateTime expiresAt;

    protected AuthSession() {}

    public AuthSession(User user, boolean firstFactorAuthenticated, boolean mfaVerified) {
        this.token = UUID.randomUUID().toString();
        this.user = user;
        this.active = true;
        this.firstFactorAuthenticated = firstFactorAuthenticated;
        this.mfaVerified = mfaVerified;
        this.expiresAt = LocalDateTime.now().plusHours(1);
    }

    public String getToken() { return token; }
    public User getUser() { return user; }
    public boolean isActive() { return active; }
    public boolean isFirstFactorAuthenticated() { return firstFactorAuthenticated; }
    public boolean isMfaVerified() { return mfaVerified; }

    public boolean isValid() {
        return active && expiresAt.isAfter(LocalDateTime.now());
    }

    public void refresh() { this.expiresAt = LocalDateTime.now().plusHours(1); }
    public void invalidate() { this.active = false; }

    public String generateOtp() {
        this.pendingOtp = String.valueOf((int) (100000 + Math.random() * 900000));
        this.otpExpiresAt = LocalDateTime.now().plusMinutes(5);
        return pendingOtp;
    }

    public boolean verifyOtp(String otp) {
        boolean ok = pendingOtp != null && pendingOtp.equals(otp) && otpExpiresAt != null && otpExpiresAt.isAfter(LocalDateTime.now());
        if (ok) {
            this.mfaVerified = true;
            this.pendingOtp = null;
            this.otpExpiresAt = null;
        }
        return ok;
    }
}
