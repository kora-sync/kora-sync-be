package com.minierp.kora_sync.dto;

import com.minierp.kora_sync.enums.EntryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Data required to create or update a financial entry (a receivable or a payable)")
public record FinancialEntryRequest(
        @Schema(description = "RECEIVABLE or PAYABLE. Cannot be changed after the entry is created",
                example = "RECEIVABLE", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        EntryType entryType,

        @Schema(description = "Customer of a RECEIVABLE. Optional; must be an active customer of the company. "
                + "Not allowed on a PAYABLE", example = "7c1e9a52-3b4d-4f60-8a2e-d5b9c0f1e377")
        UUID customerId,

        @Schema(description = "Supplier of a PAYABLE. Optional; must be an active supplier of the company. "
                + "Not allowed on a RECEIVABLE", example = "9d3f2b84-5e6a-4c71-b0d8-a1c4e7f29b56")
        UUID supplierId,

        @Schema(description = "What the entry is about", example = "Order #1042 - 10 kg of rice",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Size(max = 255)
        String description,

        @Schema(description = "Total amount, greater than zero, up to 2 decimal places", example = "150.00",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Positive
        @Digits(integer = 12, fraction = 2)
        BigDecimal amount,

        @Schema(description = "Due date (yyyy-MM-dd). A past date is accepted, for bills already overdue",
                example = "2026-12-05", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        LocalDate dueDate
) {
    public FinancialEntryRequest {
        description = description == null ? null : description.strip();
    }
}
