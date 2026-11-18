package com.minierp.kora_sync.dto;

import com.minierp.kora_sync.validation.TaxId;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Locale;

@Schema(description = "Data required to create or update a company")
public record CompanyRequest(

        @Schema(description = "Trade name shown in the system", example = "Padaria Bom Pão",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Size(max = 150)
        String name,

        @Schema(description = "Registered legal name", example = "Bom Pão Alimentos LTDA")
        @Size(max = 200)
        String legalName,

        @Schema(description = "CPF (11 digits) or CNPJ (14 characters: digits, or digits and uppercase letters "
                + "for the alphanumeric CNPJ). Punctuation is removed automatically and the check digits are "
                + "validated. Must be unique across companies",
                example = "11222333000181", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @TaxId
        String taxId,

        @Schema(description = "Contact e-mail", example = "contato@bompao.example.com")
        @Email
        @Size(max = 150)
        String email,

        @Schema(description = "Phone with area code. Non-digit characters are removed automatically",
                example = "11987654321")
        @Size(max = 30)
        String phone
) {

        public CompanyRequest {
                taxId = normalizeTaxId(taxId);
                phone = onlyDigits(phone);
        }

        private static String normalizeTaxId(String value) {
                return value == null ? null : value.replaceAll("[^0-9A-Za-z]", "").toUpperCase(Locale.ROOT);
        }

        private static String onlyDigits(String value) {
                return value == null ? null : value.replaceAll("\\D", "");
        }
}