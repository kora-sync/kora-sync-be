package com.minierp.kora_sync.service;

import com.minierp.kora_sync.dto.AuthResponse;
import com.minierp.kora_sync.dto.LoginRequest;
import com.minierp.kora_sync.dto.RegisterRequest;
import com.minierp.kora_sync.dto.UserResponse;
import com.minierp.kora_sync.enums.Role;
import com.minierp.kora_sync.exception.BusinessConflictException;
import com.minierp.kora_sync.exception.InvalidCredentialsException;
import com.minierp.kora_sync.exception.ResourceNotFoundException;
import com.minierp.kora_sync.mapper.CompanyMapper;
import com.minierp.kora_sync.mapper.UserMapper;
import com.minierp.kora_sync.model.Company;
import com.minierp.kora_sync.model.User;
import com.minierp.kora_sync.repository.CompanyRepository;
import com.minierp.kora_sync.repository.UserRepository;
import com.minierp.kora_sync.security.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final UserMapper userMapper;
    private final PasswordHasher passwordHasher;
    private final AccessTokenIssuer accessTokenIssuer;
    private final CurrentUser currentUser;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.adminEmail())) {
            throw new BusinessConflictException("E-mail already registered");
        }

        if (companyRepository.existsByTaxId(request.company().taxId())) {
            throw new BusinessConflictException("Tax id already registered: " + request.company().taxId());
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

        return authResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse signin(LoginRequest request) {
        User user = userRepository.findByEmail(request.email()).orElse(null);
        boolean passwordMatches = passwordHasher.matches(request.password(),
                user == null ? null : user.getPasswordHash());

        if (user == null || !passwordMatches || !user.isActive() || !companyRepository.existsByIdAndActiveTrue(user.getCompanyId())) {
            throw new InvalidCredentialsException();
        }

        log.info("User signed in: userId={} companyId={}", user.getId(), user.getCompanyId());

        return authResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse me() {
        AuthenticatedUser current = currentUser.get();

        User user = userRepository.findByIdAndCompanyId(current.userId(), current.companyId())
                .orElseThrow(() -> new ResourceNotFoundException("User", current.userId()));

        return userMapper.toResponse(user);
    }

    private AuthResponse authResponse(User user) {
        AccessTokenIssuer.AccessToken token = accessTokenIssuer.issue(user);
        return new AuthResponse(token.value(), "Bearer", token.expiresInSeconds(), userMapper.toResponse(user));
    }
}
