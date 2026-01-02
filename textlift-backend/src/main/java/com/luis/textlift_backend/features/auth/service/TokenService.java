package com.luis.textlift_backend.features.auth.service;

import com.luis.textlift_backend.features.auth.domain.RevokedToken;
import com.luis.textlift_backend.features.auth.repository.RevokedTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class TokenService {
    private final RevokedTokenRepository revokedTokenRepository;
    private final JwtService jwtService;

    public TokenService(RevokedTokenRepository revokedTokenRepository, JwtService jwtService) {
        this.revokedTokenRepository = revokedTokenRepository;
        this.jwtService = jwtService;
    }

    @Transactional
    public boolean isTokenRevoked(String token) {
        cleanupExpiredTokens();
        return revokedTokenRepository.existsByToken(token);
    }

    @Transactional
    public void revokeToken(String token) {
        cleanupExpiredTokens();
        if (revokedTokenRepository.existsByToken(token)) {
            return;
        }

        RevokedToken revokedToken = new RevokedToken();
        revokedToken.setToken(token);
        revokedToken.setExpiresAt(jwtService.extractExpirationDate(token).toInstant());
        revokedToken.setRevokedAt(Instant.now());
        revokedTokenRepository.save(revokedToken);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void cleanupExpiredTokens() {
        revokedTokenRepository.deleteByExpiresAtBefore(Instant.now());
    }
}
