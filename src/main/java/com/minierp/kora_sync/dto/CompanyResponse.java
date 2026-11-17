package com.minierp.kora_sync.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "A company registered in Kora Sync")
public record CompanyResponse(

        @Schema(description = "Company identifier", example = "cfbf7095-6ec9-40da-9fac-8570b746de4d")
        UUID id,

        @Schema(description = "Trade name shown in the system", example = "Padaria Bom Pão")
        String name,

        @Schema(description = "Registered legal name", example = "Bom Pão Alimentos LTDA")
        String legalName,

        @Schema(description = "CNPJ or CPF, digits only", example = "11222333000181")
        String taxId,

        @Schema(description = "Contact e-mail", example = "contato@bompao.example.com")
        String email,

        @Schema(description = "Phone with area code, digits only", example = "11987654321")
        String phone,

        @Schema(description = "Whether the company is active. Deactivated companies are kept for history",
                example = "true")
        boolean active,

        @Schema(description = "Creation instant, in UTC", example = "2026-11-17T20:26:44.539Z")
        Instant createdAt,

        @Schema(description = "Last update instant, in UTC", example = "2026-11-18T13:05:10.120Z")
        Instant updatedAt
) {
}