package com.minierp.kora_sync.dto;

import com.minierp.kora_sync.enums.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Realized money moved through one payment method in the period")
public record PaymentMethodFlowResponse(

        @Schema(description = "Payment method", example = "PIX")
        PaymentMethod paymentMethod,

        @Schema(description = "Money received with this method", example = "500.00")
        BigDecimal inflow,

        @Schema(description = "Money paid with this method", example = "0.00")
        BigDecimal outflow
) {
}