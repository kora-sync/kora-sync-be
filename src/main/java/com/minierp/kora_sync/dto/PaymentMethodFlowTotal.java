package com.minierp.kora_sync.dto;

import com.minierp.kora_sync.enums.EntryType;
import com.minierp.kora_sync.enums.PaymentMethod;

import java.math.BigDecimal;

public record PaymentMethodFlowTotal(
        PaymentMethod paymentMethod,
        EntryType entryType,
        BigDecimal total
) {
}
