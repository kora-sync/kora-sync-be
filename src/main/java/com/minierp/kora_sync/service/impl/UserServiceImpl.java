package com.minierp.kora_sync.service.impl;

import com.minierp.kora_sync.dto.CreateUserRequest;
import com.minierp.kora_sync.dto.PageResponse;
import com.minierp.kora_sync.dto.ResetPasswordRequest;
import com.minierp.kora_sync.dto.UpdateUserRequest;
import com.minierp.kora_sync.dto.UserResponse;
import com.minierp.kora_sync.enums.Role;
import com.minierp.kora_sync.exception.BusinessConflictException;
import com.minierp.kora_sync.exception.ResourceNotFoundException;
import com.minierp.kora_sync.mapper.UserMapper;
import com.minierp.kora_sync.model.User;
import com.minierp.kora_sync.repository.CompanyRepository;
import com.minierp.kora_sync.repository.RefreshTokenRepository;
import com.minierp.kora_sync.repository.UserRepository;
import com.minierp.kora_sync.security.PasswordHasher;
import com.minierp.kora_sync.service.UserService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserMapper userMapper;
    private final PasswordHasher passwordHasher;
    private final Clock clock;


    @Override
    @Transactional
    public UserResponse create(UUID companyId, CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessConflictException("E-mail already registered");
        }

        User user = new User();
        user.setCompanyId(companyId);
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPasswordHash(passwordHasher.hash(request.password()));
        user.setRole(request.role());
        User saved = userRepository.save(user);

        log.info("User created: userId={} companyId={} role={}", saved.getId(), companyId, saved.getRole());
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> findAll(UUID companyId, String name, Role role, Boolean active, Pageable pageable) {
        Page<User> page = userRepository.findAll(filter(companyId, name, role, active), pageable);
        return PageResponse.from(page.map(userMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findGetById(UUID companyId, UUID id) {
        return userRepository.findByIdAndCompanyId(id, companyId)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    @Override
    @Transactional
    public UserResponse update(UUID companyId, UUID id, UpdateUserRequest request) {
        User user = lockUser(companyId, id);

        if (request.role() != Role.ADMIN) {
            requireAnotherActiveAdmin(companyId, user);
        }

        user.setName(request.name());
        user.setRole(request.role());

        log.info("User updated: userId={} companyId={} role={}", id, companyId, request.role());
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void deactivate(UUID companyId, UUID id) {
        User user = lockUser(companyId, id);
        if (!user.isActive()) {
            return;
        }
        requireAnotherActiveAdmin(companyId, user);
        user.setActive(false);
        refreshTokenRepository.revokeAllByUser(id, clock.instant());
        log.info("User deactivated: userId={} companyId={}", id, companyId);
    }

    @Override
    @Transactional
    public void activate(UUID companyId, UUID id) {
        User user = lockUser(companyId, id);
        if (!user.isActive()) {
            user.setActive(true);
            log.info("User reactivated: userId={} companyId={}", id, companyId);
        }
    }

    @Override
    @Transactional
    public void resetPassword(UUID companyId, UUID id, ResetPasswordRequest request) {
        User user = lockUser(companyId, id);
        user.setPasswordHash(passwordHasher.hash(request.password()));
        refreshTokenRepository.revokeAllByUser(id, clock.instant());
        log.info("Password reset by an administrator: userId={} companyId={}", id, companyId);
    }

    /**
     * Locks the company row first and the user row second, always in this order, so that two
     * administrator changes in the same company are serialized and never deadlock.
     */
    private User lockUser(UUID companyId, UUID id) {
        companyRepository.findByIdForUpdate(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", companyId));
        return userRepository.findByIdAndCompanyIdForUpdate(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    private void requireAnotherActiveAdmin(UUID companyId, User user) {
        boolean removesAnAdmin = user.isActive() && user.getRole() == Role.ADMIN;
        if (removesAnAdmin && userRepository.countByCompanyIdAndRoleAndActiveTrue(companyId, Role.ADMIN) <= 1) {
            throw new BusinessConflictException("The company must keep at least one active administrator");
        }
    }

    private Specification<User> filter(UUID companyId, String name, Role role, Boolean active) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("companyId"), companyId));
            if (name != null && !name.isBlank()) {
                predicates.add(builder.like(builder.lower(root.get("name")),
                        "%" + name.strip().toLowerCase(Locale.ROOT) + "%"));
            }
            if (role != null) {
                predicates.add(builder.equal(root.get("role"), role));
            }
            if (active != null) {
                predicates.add(builder.equal(root.get("active"), active));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
