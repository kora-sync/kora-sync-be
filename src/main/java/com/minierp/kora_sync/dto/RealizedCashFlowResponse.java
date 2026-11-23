package com.minierp.kora_sync.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "Realized cash flow: money that actually moved, based on settlements and their settlement date")
public record RealizedCashFlowResponse(

        @Schema(description = "First day of the period (inclusive)", example = "2026-11-01")
        LocalDate from,

        @Schema(description = "Last day of the period (inclusive)", example = "2026-11-30")
        LocalDate to,

        @Schema(description = "Total received in the period", example = "600.00")
        BigDecimal totalInflow,

        @Schema(description = "Total paid in the period", example = "150.00")
        BigDecimal totalOutflow,

        @Schema(description = "Total received minus total paid", example = "450.00")
        BigDecimal net,

        @Schema(description = "One item per day of the period, in chronological order")
        List<CashFlowDayResponse> days,

        @Schema(description = "Totals per payment method, only methods with movement, in the enum order")
        List<PaymentMethodFlowResponse> byPaymentMethod
) {
}