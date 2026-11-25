package com.minierp.kora_sync.service;

import com.minierp.kora_sync.dto.*;
import com.minierp.kora_sync.enums.Role;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    UserResponse create(UUID companyId, CreateUserRequest request);
    PageResponse<UserResponse> findAll(UUID companyId, String name, Role role, Boolean active, Pageable pageable);
    UserResponse findGetById(UUID companyId, UUID id);
    UserResponse update(UUID companyId, UUID id, UpdateUserRequest request);
    void deactivate(UUID companyId, UUID id);
    void activate(UUID companyId, UUID id);
    void resetPassword(UUID companyId, UUID id, ResetPasswordRequest request);
}
