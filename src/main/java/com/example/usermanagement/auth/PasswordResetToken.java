package com.example.usermanagement.auth;

import com.example.usermanagement.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    private String token;

    @ManyToOne(optional = false)
    private User user;

    private boolean used;
    private LocalDateTime expiresAt;

    protected PasswordResetToken() {}

    public PasswordResetToken(User user) {
        this.token = UUID.randomUUID().toString();
        this.user = user;
        this.used = false;
        this.expiresAt = LocalDateTime.now().plusMinutes(30);
    }

    public String getToken() { return token; }
    public User getUser() { return user; }
    public boolean isUsable() { return !used && expiresAt.isAfter(LocalDateTime.now()); }
    public void markUsed() { this.used = true; }
}
