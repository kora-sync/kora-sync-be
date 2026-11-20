package com.minierp.kora_sync.dto;

import com.minierp.kora_sync.enums.EntryStatus;
import com.minierp.kora_sync.enums.EntryType;

import java.time.LocalDate;
import java.util.UUID;

public record FinancialEntryFilter(
        EntryType entryType,
        EntryStatus status,
        UUID customerId,
        UUID supplierId,
        LocalDate dueFrom,
        LocalDate dueTo,
        Boolean overdue) {
}