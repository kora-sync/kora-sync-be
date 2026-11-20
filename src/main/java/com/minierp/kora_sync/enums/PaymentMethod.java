package com.minierp.kora_sync.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = """
        How a settlement was paid or received. \
        CASH: cash. PIX: Pix instant payment. CREDIT_CARD: credit card. DEBIT_CARD: debit card. \
        BANK_SLIP: boleto. BANK_TRANSFER: bank transfer (TED/DOC).""")
public enum PaymentMethod {
    CASH,
    PIX,
    CREDIT_CARD,
    DEBIT_CARD,
    BANK_SLIP,
    BANK_TRANSFER
}