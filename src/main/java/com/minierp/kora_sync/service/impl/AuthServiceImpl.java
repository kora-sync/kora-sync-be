package com.minierp.kora_sync.service.impl;

import com.minierp.kora_sync.dto.*;
import com.minierp.kora_sync.enums.Role;
import com.minierp.kora_sync.exception.BusinessConflictException;
import com.minierp.kora_sync.exception.InvalidCredentialsException;
import com.minierp.kora_sync.exception.InvalidInputException;
import com.minierp.kora_sync.exception.ResourceNotFoundException;
import com.minierp.kora_sync.mapper.CompanyMapper;
import com.minierp.kora_sync.mapper.UserMapper;
import com.minierp.kora_sync.model.Company;
import com.minierp.kora_sync.model.RefreshToken;
import com.minierp.kora_sync.model.User;
import com.minierp.kora_sync.repository.CompanyRepository;
import com.minierp.kora_sync.repository.RefreshTokenRepository;
import com.minierp.kora_sync.repository.UserRepository;
import com.minierp.kora_sync.security.AccessTokenIssuer;
import com.minierp.kora_sync.security.AuthenticatedUser;
import com.minierp.kora_sync.security.CurrentUser;
import com.minierp.kora_sync.security.JwtProperties;
import com.minierp.kora_sync.security.PasswordHasher;
import com.minierp.kora_sync.security.RefreshTokenGenerator;
import com.minierp.kora_sync.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CompanyMapper companyMapper;
    private final UserMapper userMapper;
    private final PasswordHasher passwordHasher;
    private final AccessTokenIssuer accessTokenIssuer;
    private final RefreshTokenGenerator refreshTokenGenerator;
    private final JwtProperties jwtProperties;
    private final CurrentUser currentUser;
    private final Clock clock;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.adminEmail())) {
            throw new BusinessConflictException("E-mail already registered");
        }
        if (companyRepository.existsByTaxId(request.company().taxId())) {
            throw new BusinessConflictException(
                    "Tax id already registered: " + request.company().taxId());
        }

        Company company = companyRepository.save(companyMapper.toEntity(request.company()));

        User admin = new User();
        admin.setCompanyId(company.getId());
        admin.setName(request.adminName());
        admin.setEmail(request.adminEmail());
        admin.setPasswordHash(passwordHasher.hash(request.adminPassword()));
        admin.setRole(Role.ADMIN);
        User saved = userRepository.save(admin);

        log.info("Company registered: companyId={} adminUserId={}", company.getId(), saved.getId());
        return authResponse(saved, issueRefreshToken(saved.getId(), UUID.randomUUID()));
    }

    @Override
    @Transactional
    public AuthResponse signin(LoginRequest request) {
        User user = userRepository.findByEmail(request.email()).orElse(null);
        boolean passwordMatches = passwordHasher.matches(request.password(),
                user == null ? null : user.getPasswordHash());

        if (user == null
                || !passwordMatches
                || !user.isActive()
                || !companyRepository.existsByIdAndActiveTrue(user.getCompanyId())) {
            throw new InvalidCredentialsException();
        }
        log.info("User signed in: userId={} companyId={}", user.getId(), user.getCompanyId());
        return authResponse(user, issueRefreshToken(user.getId(), UUID.randomUUID()));
    }

    @Override
    @Transactional(noRollbackFor = InvalidCredentialsException.class)
    public AuthResponse refresh(RefreshTokenRequest request) {
        Instant now = clock.instant();
        RefreshToken current = refreshTokenRepository
                .findByTokenHashForUpdate(refreshTokenGenerator.hash(request.refreshToken()))
                .orElseThrow(this::invalidRefreshToken);

        if (current.getRevokedAt() != null) {
            log.warn("Refresh token reuse detected, revoking family: userId={} familyId={}",
                    current.getUserId(), current.getFamilyId());
            refreshTokenRepository.revokeFamily(current.getFamilyId(), now);
            throw invalidRefreshToken();
        }
        if (!current.getExpiresAt().isAfter(now)) {
            throw invalidRefreshToken();
        }

        User user = userRepository.findById(current.getUserId())
                .orElseThrow(this::invalidRefreshToken);
        if (!user.isActive() || !companyRepository.existsByIdAndActiveTrue(user.getCompanyId())) {
            refreshTokenRepository.revokeFamily(current.getFamilyId(), now);
            throw invalidRefreshToken();
        }

        current.setRevokedAt(now);
        String next = issueRefreshToken(user.getId(), current.getFamilyId());
        return authResponse(user, next);
    }

    @Override
    @Transactional
    public void logout(RefreshTokenRequest request) {
        refreshTokenRepository.findByTokenHash(refreshTokenGenerator.hash(request.refreshToken()))
                .ifPresent(token -> {
                    refreshTokenRepository.revokeFamily(token.getFamilyId(), clock.instant());
                    log.info("User signed out: userId={}", token.getUserId());
                });
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse me() {
        AuthenticatedUser current = currentUser.get();
        return userRepository.findByIdAndCompanyId(current.userId(), current.companyId())
                .filter(User::isActive)
                .map(userMapper::toResponse)
                .orElseThrow(this::noLongerAuthenticated);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        AuthenticatedUser current = currentUser.get();
        User user = userRepository.findByIdAndCompanyIdForUpdate(current.userId(), current.companyId())
                .filter(User::isActive)
                .orElseThrow(this::noLongerAuthenticated);

        if (!passwordHasher.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new InvalidInputException("The current password is incorrect");
        }
        if (passwordHasher.matches(request.newPassword(), user.getPasswordHash())) {
            throw new InvalidInputException("The new password must be different from the current one");
        }

        user.setPasswordHash(passwordHasher.hash(request.newPassword()));
        refreshTokenRepository.revokeAllByUser(user.getId(), clock.instant());
        log.info("Password changed by the user: userId={}", user.getId());
    }

    private InvalidCredentialsException noLongerAuthenticated() {
        return new InvalidCredentialsException("The user of the access token no longer exists or is inactive");
    }

    private String issueRefreshToken(UUID userId, UUID familyId) {
        String rawToken = refreshTokenGenerator.generate();
        RefreshToken token = new RefreshToken();
        token.setUserId(userId);
        token.setFamilyId(familyId);
        token.setTokenHash(refreshTokenGenerator.hash(rawToken));
        token.setExpiresAt(clock.instant().plus(jwtProperties.refreshTokenTtl()));
        refreshTokenRepository.save(token);
        return rawToken;
    }

    private AuthResponse authResponse(User user, String refreshToken) {
        AccessTokenIssuer.AccessToken accessToken = accessTokenIssuer.issue(user);
        return new AuthResponse(
                accessToken.value(),
                "Bearer",
                accessToken.expiresInSeconds(),
                refreshToken,
                jwtProperties.refreshTokenTtl().toSeconds(),
                userMapper.toResponse(user));
    }

    private InvalidCredentialsException invalidRefreshToken() {
        return new InvalidCredentialsException("Invalid or expired refresh token");
    }
}