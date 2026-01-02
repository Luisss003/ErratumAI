package com.luis.textlift_backend.features.auth.repository;

import com.luis.textlift_backend.features.auth.domain.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.UUID;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, UUID> {
    boolean existsByToken(String token);
    long deleteByExpiresAtBefore(Instant expiresAt);
}
