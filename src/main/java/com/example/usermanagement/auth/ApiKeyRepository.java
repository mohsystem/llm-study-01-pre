package com.example.usermanagement.auth;

import com.example.usermanagement.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, String> {
    List<ApiKey> findByOwner(User owner);
    Optional<ApiKey> findByKeyIdAndOwner(String keyId, User owner);
}
