package com.minierp.kora_sync.dto;

import com.minierp.kora_sync.enums.MovementType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Data required to register a stock movement for a product")
public record StockMovementRequest(

        @Schema(description = "Kind of movement", example = "ENTRY", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        MovementType movementType,

        @Schema(description = "For ENTRY and EXIT: the amount received or removed, greater than zero. "
                + "For ADJUSTMENT: the counted balance, zero or more. Up to 3 decimal places; "
                + "products measured in UN, CX or PCT accept whole numbers only",
                example = "20.000", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @PositiveOrZero
        @Digits(integer = 11, fraction = 3)
        BigDecimal quantity,

        @Schema(description = "Why the movement happened. Required for ADJUSTMENT, optional otherwise",
                example = "Weekly delivery from supplier")
        @Size(max = 255)
        String reason
) {

    public StockMovementRequest {
        reason = InputNormalizer.emptyToNull(reason == null ? null : reason.strip());
    }
}