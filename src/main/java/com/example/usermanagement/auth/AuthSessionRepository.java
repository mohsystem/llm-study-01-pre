package com.example.usermanagement.auth;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthSessionRepository extends JpaRepository<AuthSession, String> {
}
