package com.minierp.kora_sync.enums;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;
import java.util.List;

@Schema(description = """
        Lifecycle of a financial entry. \
        OPEN: nothing settled yet; can be edited or canceled. \
        PARTIALLY_SETTLED: part of the amount was settled; still accepts settlements. \
        SETTLED: fully settled. \
        CANCELED: canceled before any settlement; kept for history.""")
public enum EntryStatus {

    OPEN(true, true, true),
    PARTIALLY_SETTLED(true, false, false),
    SETTLED(false, false, false),
    CANCELED(false, false, false);

    private final boolean settleable;
    private final boolean editable;
    private final boolean cancelable;

    EntryStatus(boolean settleable, boolean editable, boolean cancelable) {
        this.settleable = settleable;
        this.editable = editable;
        this.cancelable = cancelable;
    }

    public boolean isSettleable() {
        return settleable;
    }

    public boolean isEditable() {
        return editable;
    }

    public boolean isCancelable() {
        return cancelable;
    }

    public static List<EntryStatus> settleableStatuses() {
        return Arrays.stream(values()).filter(EntryStatus::isSettleable).toList();
    }
}