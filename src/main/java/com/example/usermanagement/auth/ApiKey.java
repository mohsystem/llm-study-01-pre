package com.example.usermanagement.auth;

import com.example.usermanagement.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "api_keys")
public class ApiKey {
    @Id
    private String keyId;

    @Column(nullable = false, unique = true)
    private String keyValue;

    @ManyToOne(optional = false)
    private User owner;

    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime revokedAt;

    protected ApiKey() {}

    public ApiKey(User owner) {
        this.keyId = UUID.randomUUID().toString();
        this.keyValue = UUID.randomUUID().toString().replace("-", "");
        this.owner = owner;
        this.status = "ACTIVE";
        this.createdAt = LocalDateTime.now();
    }

    public String getKeyId() { return keyId; }
    public String getKeyValue() { return keyValue; }
    public User getOwner() { return owner; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void revoke() {
        this.status = "REVOKED";
        this.revokedAt = LocalDateTime.now();
    }
}
