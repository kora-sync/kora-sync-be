package com.minierp.kora_sync.dto;

public record ProductFilter(
        String name,
        String sku,
        Boolean active,
        Boolean lowStock
) {
}
