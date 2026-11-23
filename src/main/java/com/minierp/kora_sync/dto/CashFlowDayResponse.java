package com.minierp.kora_sync.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Cash flow of one day. Days without movement are included with zero values")
public record CashFlowDayResponse(

        @Schema(description = "The day", example = "2026-11-18")
        LocalDate date,

        @Schema(description = "Money coming in on this day (receivables)", example = "300.00")
        BigDecimal inflow,

        @Schema(description = "Money going out on this day (payables)", example = "150.00")
        BigDecimal outflow,

        @Schema(description = "Inflow minus outflow of this day", example = "150.00")
        BigDecimal net,

        @Schema(description = "Running total of the net values from the first day of the period up to this day. "
                + "There is no opening balance", example = "450.00")
        BigDecimal cumulativeNet
) {
}