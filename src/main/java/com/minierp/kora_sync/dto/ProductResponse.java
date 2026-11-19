package com.minierp.kora_sync.dto;

import com.minierp.kora_sync.enums.UnitOfMeasure;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "A product of a company")
public record ProductResponse(

        @Schema(description = "Product identifier", example = "4e8b7d1a-6c2f-4a39-b5d0-91f3a7c2e864")
        UUID id,

        @Schema(description = "Identifier of the company that owns the product",
                example = "cfbf7095-6ec9-40da-9fac-8570b746de4d")
        UUID companyId,

        @Schema(description = "Internal product code", example = "PAO-FRANCES")
        String sku,

        @Schema(description = "Product name", example = "Pão francês")
        String name,

        @Schema(description = "Optional description", example = "Pão francês tradicional, vendido por quilo")
        String description,

        @Schema(description = "Unit of measure", example = "KG")
        UnitOfMeasure unit,

        @Schema(description = "Unit cost", example = "12.50")
        BigDecimal costPrice,

        @Schema(description = "Sale price per unit", example = "18.90")
        BigDecimal salePrice,

        @Schema(description = "Current stock balance. It only changes through stock movements", example = "42.500")
        BigDecimal stockQuantity,

        @Schema(description = "Minimum stock level. 0 means no alert", example = "5.000")
        BigDecimal minimumStock,

        @Schema(description = "True when a minimum is configured and the balance is at or below it",
                example = "false")
        boolean lowStock,

        @Schema(description = "Whether the product is active. Deactivated products are kept for history",
                example = "true")
        boolean active,

        @Schema(description = "Creation instant, in UTC", example = "2026-11-18T14:20:10.500Z")
        Instant createdAt,

        @Schema(description = "Last update instant, in UTC", example = "2026-11-18T15:02:44.120Z")
        Instant updatedAt
) {
}