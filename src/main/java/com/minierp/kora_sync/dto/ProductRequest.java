package com.minierp.kora_sync.dto;

import com.minierp.kora_sync.enums.UnitOfMeasure;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Data required to create or update a product. The stock balance is not part of this "
        + "payload: it only changes through stock movements")
public record ProductRequest(

        @Schema(description = "Internal product code, unique within the company. Letters, digits, dot, hyphen and "
                + "underscore only; converted to lowercase automatically",
                example = "pao-frances", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Size(max = 50)
        @Pattern(regexp = "[a-z0-9._-]+", message = "must contain only letters, digits, '.', '-' or '_'")
        String sku,

        @Schema(description = "Product name", example = "Pão francês", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Size(max = 150)
        String name,

        @Schema(description = "Optional free-text description", example = "Pão francês tradicional, vendido por quilo")
        @Size(max = 500)
        String description,

        @Schema(description = "Unit of measure", example = "KG", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        UnitOfMeasure unit,

        @Schema(description = "Unit cost, up to 2 decimal places. Defaults to 0 when omitted", example = "12.50")
        @PositiveOrZero
        @Digits(integer = 12, fraction = 2)
        BigDecimal costPrice,

        @Schema(description = "Sale price per unit, up to 2 decimal places", example = "18.90",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @PositiveOrZero
        @Digits(integer = 12, fraction = 2)
        BigDecimal salePrice,

        @Schema(description = "Minimum stock level, up to 3 decimal places. Use 0 for no alert. "
                + "Defaults to 0 when omitted", example = "5.000")
        @PositiveOrZero
        @Digits(integer = 11, fraction = 3)
        BigDecimal minimumStock
) {

    public ProductRequest {
        sku = InputNormalizer.code(sku);
        costPrice = costPrice == null ? BigDecimal.ZERO : costPrice;
        minimumStock = minimumStock == null ? BigDecimal.ZERO : minimumStock;
    }
}