package com.minierp.kora_sync.dto;

import com.minierp.kora_sync.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "A user of the system. The password is never returned")
public record UserResponse(

        @Schema(description = "User identifier", example = "0d6c8e2a-4b1f-4c93-a7e5-3f9b1d5c7a20")
        UUID id,

        @Schema(description = "Company the user belongs to", example = "cfbf7095-6ec9-40da-9fac-8570b746de4d")
        UUID companyId,

        @Schema(description = "User name", example = "Ana Souza")
        String name,

        @Schema(description = "Login e-mail, in lowercase", example = "ana@padariakora.com.br")
        String email,

        @Schema(description = "Access level", example = "ADMIN")
        Role role,

        @Schema(description = "Whether the user can sign in", example = "true")
        boolean active,

        @Schema(description = "Creation instant, in UTC", example = "2026-11-23T21:10:05.300Z")
        Instant createdAt
) {
}