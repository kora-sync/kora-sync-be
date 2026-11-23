package com.minierp.kora_sync.dto;

import com.minierp.kora_sync.enums.EntryType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyFlowTotal(
        LocalDate day,
        EntryType entryType,
        BigDecimal total
) {
}
