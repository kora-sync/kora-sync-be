package com.minierp.kora_sync.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "Forecast cash flow: what is still to be received or paid, by due date. Only OPEN and "
        + "PARTIALLY_SETTLED entries count, using their remaining amount")
public record ForecastCashFlowResponse(

        @Schema(description = "First day of the period (inclusive)", example = "2026-12-01")
        LocalDate from,

        @Schema(description = "Last day of the period (inclusive)", example = "2026-12-31")
        LocalDate to,

        @Schema(description = "Total still to be received, due in the period", example = "280.00")
        BigDecimal totalInflow,

        @Schema(description = "Total still to be paid, due in the period", example = "370.00")
        BigDecimal totalOutflow,

        @Schema(description = "Expected inflow minus expected outflow", example = "-90.00")
        BigDecimal net,

        @Schema(description = "One item per day of the period, in chronological order")
        List<CashFlowDayResponse> days
) {
}