package com.minierp.kora_sync.dto;

import java.util.Locale;

public final class InputNormalizer {
    private InputNormalizer() {}

    static String taxId(String value) {
        return emptyToNull(value == null ? null
                : value.replaceAll("[^0-9A-Za-z]", "").toUpperCase(Locale.ROOT));
    }

    static String digits(String value) {
        return emptyToNull(value == null ? null : value.replaceAll("\\D", ""));
    }

    private static String emptyToNull(String value) {
        return value == null || value.isEmpty() ? null : value;
    }
}
