package com.minierp.kora_sync.dto;

import com.minierp.kora_sync.enums.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Data required to settle (pay or receive) part or all of a financial entry")
public record SettlementRequest(

        @Schema(description = "Amount settled, greater than zero, up to 2 decimal places. "
                + "It cannot exceed the remaining amount of the entry",
                example = "50.00", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Positive
        @Digits(integer = 12, fraction = 2)
        BigDecimal amount,

        @Schema(description = "Day the money was actually paid or received (yyyy-MM-dd). "
                + "Defaults to today; a future date is not accepted", example = "2026-11-20")
        LocalDate settledOn,

        @Schema(description = "How it was paid or received", example = "PIX",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        PaymentMethod paymentMethod,

        @Schema(description = "Optional free-text note", example = "Paid at the counter")
        @Size(max = 255)
        String notes
) {

    public SettlementRequest {
        notes = InputNormalizer.emptyToNull(notes == null ? null : notes.strip());
    }
}