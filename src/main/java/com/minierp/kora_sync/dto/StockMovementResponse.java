package com.minierp.kora_sync.dto;

import com.minierp.kora_sync.enums.MovementType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "A recorded stock movement. Movements are never changed or removed")
public record StockMovementResponse(

        @Schema(description = "Movement identifier", example = "b7a1c3e5-2d4f-4a68-9c0b-5e7f1a3d9c42")
        UUID id,

        @Schema(description = "Product the movement belongs to", example = "4e8b7d1a-6c2f-4a39-b5d0-91f3a7c2e864")
        UUID productId,

        @Schema(description = "Kind of movement", example = "EXIT")
        MovementType movementType,

        @Schema(description = "Signed change applied to the balance: positive adds, negative removes",
                example = "-17.500")
        BigDecimal quantity,

        @Schema(description = "Stock balance right after this movement", example = "2.500")
        BigDecimal balanceAfter,

        @Schema(description = "Reason informed, when any", example = "Sale #1042")
        String reason,

        @Schema(description = "When the movement was recorded, in UTC", example = "2026-11-19T20:15:30.250Z")
        Instant createdAt
) {
}