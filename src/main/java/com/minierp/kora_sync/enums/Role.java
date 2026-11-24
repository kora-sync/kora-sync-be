package com.minierp.kora_sync.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = """
        Access level of a user. \
        ADMIN: full access, including users and company settings. \
        MANAGER: full operational access (customers, suppliers, products, stock, financial, reports). \
        OPERATOR: reads operational data and registers stock movements and settlements.""")
public enum Role {
    ADMIN,
    MANAGER,
    OPERATOR
}