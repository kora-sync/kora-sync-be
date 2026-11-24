package com.minierp.kora_sync.security;

import com.minierp.kora_sync.enums.Role;

import java.util.UUID;

public record AuthenticatedUser(
        UUID userId,
        UUID companyId,
        Role role
) {
}
