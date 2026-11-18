package com.minierp.kora_sync.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "A customer of a company")
public record CustomerResponse(

        @Schema(description = "Customer identifier", example = "7c9e6679-7425-40de-944b-e07fc1f90ae7")
        UUID id,

        @Schema(description = "Identifier of the company that owns the customer",
                example = "cfbf7095-6ec9-40da-9fac-8570b746de4d")
        UUID companyId,

        @Schema(description = "Customer full name or trade name", example = "Maria Silva")
        String name,

        @Schema(description = "CPF or CNPJ without punctuation, when informed", example = "52998224725")
        String document,

        @Schema(description = "Contact e-mail", example = "maria.silva@example.com")
        String email,

        @Schema(description = "Phone with area code, digits only", example = "11987654321")
        String phone,

        @Schema(description = "Whether the customer is active. Deactivated customers are kept for history",
                example = "true")
        boolean active,

        @Schema(description = "Creation instant, in UTC", example = "2026-11-18T14:20:10.500Z")
        Instant createdAt,

        @Schema(description = "Last update instant, in UTC", example = "2026-11-18T15:02:44.120Z")
        Instant updatedAt
) {
}