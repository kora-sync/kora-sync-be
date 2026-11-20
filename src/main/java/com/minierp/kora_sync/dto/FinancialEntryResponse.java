package com.minierp.kora_sync.dto;

import com.minierp.kora_sync.enums.EntryStatus;
import com.minierp.kora_sync.enums.EntryType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "A financial entry: an amount the company will receive or pay")
public record FinancialEntryResponse(

        @Schema(description = "Entry identifier", example = "e2a4c6d8-1f3b-4957-8c0e-6b8d0a2f4e91")
        UUID id,

        @Schema(description = "Identifier of the company that owns the entry",
                example = "cfbf7095-6ec9-40da-9fac-8570b746de4d")
        UUID companyId,

        @Schema(description = "RECEIVABLE or PAYABLE", example = "RECEIVABLE")
        EntryType entryType,

        @Schema(description = "Customer, when informed on a RECEIVABLE", example = "7c1e9a52-3b4d-4f60-8a2e-d5b9c0f1e377",
                nullable = true)
        UUID customerId,

        @Schema(description = "Supplier, when informed on a PAYABLE", example = "9d3f2b84-5e6a-4c71-b0d8-a1c4e7f29b56",
                nullable = true)
        UUID supplierId,

        @Schema(description = "What the entry is about", example = "Order #1042 - 10 kg of rice")
        String description,

        @Schema(description = "Total amount", example = "150.00")
        BigDecimal amount,

        @Schema(description = "Amount already settled", example = "50.00")
        BigDecimal settledAmount,

        @Schema(description = "Amount still to be settled (amount minus settledAmount)", example = "100.00")
        BigDecimal remainingAmount,

        @Schema(description = "Due date", example = "2026-12-05")
        LocalDate dueDate,

        @Schema(description = "Current status", example = "PARTIALLY_SETTLED")
        EntryStatus status,

        @Schema(description = "True when the entry still accepts settlements and its due date is before today",
                example = "false")
        boolean overdue,

        @Schema(description = "Creation instant, in UTC", example = "2026-11-20T13:02:10.500Z")
        Instant createdAt,

        @Schema(description = "Last update instant, in UTC", example = "2026-11-20T14:15:44.120Z")
        Instant updatedAt
) {
}