package com.minierp.kora_sync.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TaxIdValidator implements ConstraintValidator<TaxId, String> {

    private static final int[] CPF_WEIGHTS_1 = {10, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] CPF_WEIGHTS_2 = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] CNPJ_WEIGHTS_1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] CNPJ_WEIGHTS_2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        return switch (value.length()) {
            case 11 -> isValidCpf(value);
            case 14 -> isValidCnpj(value);
            default -> false;
        };
    }

    static boolean isValidCpf(String cpf) {
        if (!cpf.chars().allMatch(c -> isDigit((char) c)) || hasOnlyOneDistinctChar(cpf)) {
            return false;
        }
        return checkDigit(cpf, CPF_WEIGHTS_1) == cpf.charAt(9) - '0'
                && checkDigit(cpf, CPF_WEIGHTS_2) == cpf.charAt(10) - '0';
    }

    static boolean isValidCnpj(String cnpj) {
        if (hasOnlyOneDistinctChar(cnpj)) {
            return false;
        }
        for (int i = 0; i < 12; i++) {
            char c = cnpj.charAt(i);
            if (!isDigit(c) && !(c >= 'A' && c <= 'Z')) {
                return false;
            }
        }
        if (!isDigit(cnpj.charAt(12)) || !isDigit(cnpj.charAt(13))) {
            return false;
        }
        return checkDigit(cnpj, CNPJ_WEIGHTS_1) == cnpj.charAt(12) - '0'
                && checkDigit(cnpj, CNPJ_WEIGHTS_2) == cnpj.charAt(13) - '0';
    }

    private static int checkDigit(String value, int[] weights) {
        int sum = 0;
        for (int i = 0; i < weights.length; i++) {
            sum += (value.charAt(i) - '0') * weights[i];
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }

    private static boolean hasOnlyOneDistinctChar(String value) {
        return value.chars().distinct().count() == 1;
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
}