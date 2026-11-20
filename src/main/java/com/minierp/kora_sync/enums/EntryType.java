package com.minierp.kora_sync.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = """
        Direction of a financial entry. \
        RECEIVABLE: money the company will receive, usually from a customer. \
        PAYABLE: money the company will pay, usually to a supplier.""")
public enum EntryType {
    RECEIVABLE,
    PAYABLE
}
