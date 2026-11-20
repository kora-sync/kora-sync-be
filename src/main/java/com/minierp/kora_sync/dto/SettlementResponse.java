package com.minierp.kora_sync.dto;

import com.minierp.kora_sync.enums.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "A recorded settlement. Settlements are never changed or removed")
public record SettlementResponse(

        @Schema(description = "Settlement identifier", example = "3f5b7d9a-2c4e-4a68-b1d0-8e6a0c2f4b73")
        UUID id,

        @Schema(description = "Financial entry the settlement belongs to",
                example = "e2a4c6d8-1f3b-4957-8c0e-6b8d0a2f4e91")
        UUID entryId,

        @Schema(description = "Amount settled", example = "50.00")
        BigDecimal amount,

        @Schema(description = "Day the money was paid or received", example = "2026-11-20")
        LocalDate settledOn,

        @Schema(description = "How it was paid or received", example = "PIX")
        PaymentMethod paymentMethod,

        @Schema(description = "Optional note", example = "Paid at the counter", nullable = true)
        String notes,

        @Schema(description = "When the settlement was recorded, in UTC", example = "2026-11-20T15:30:00.250Z")
        Instant createdAt
) {
}