package com.minierp.kora_sync.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "A supplier of a company")
public record SupplierResponse(

        @Schema(description = "Supplier identifier", example = "9b2f4c1e-3a7d-4f60-8e55-2d1c7a9b6f03")
        UUID id,

        @Schema(description = "Identifier of the company that owns the supplier",
                example = "cfbf7095-6ec9-40da-9fac-8570b746de4d")
        UUID companyId,

        @Schema(description = "Supplier name or trade name", example = "Distribuidora Pão Dourado")
        String name,

        @Schema(description = "CPF or CNPJ without punctuation, when informed", example = "11222333000181")
        String document,

        @Schema(description = "Contact e-mail", example = "vendas@paodourado.example.com")
        String email,

        @Schema(description = "Phone with area code, digits only", example = "1133334444")
        String phone,

        @Schema(description = "Whether the supplier is active. Deactivated suppliers are kept for history",
                example = "true")
        boolean active,

        @Schema(description = "Creation instant, in UTC", example = "2026-11-18T14:20:10.500Z")
        Instant createdAt,

        @Schema(description = "Last update instant, in UTC", example = "2026-11-18T15:02:44.120Z")
        Instant updatedAt
) {
}