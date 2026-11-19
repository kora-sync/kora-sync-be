package com.minierp.kora_sync.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = """
        Kind of stock movement. \
        ENTRY: goods received (purchase or restock); increases the balance. \
        EXIT: goods leaving (sale or loss); decreases the balance and is refused if it would make it negative. \
        ADJUSTMENT: inventory count correction; the quantity informed is the counted balance and a reason is required.""")
public enum MovementType {

    ENTRY(false),
    EXIT(false),
    ADJUSTMENT(true);

    private final boolean reasonRequired;

    MovementType(boolean reasonRequired) {
        this.reasonRequired = reasonRequired;
    }

    public boolean isReasonRequired() {
        return reasonRequired;
    }
}