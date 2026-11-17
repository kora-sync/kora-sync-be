package com.minierp.kora_sync.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

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

        @Schema(description = "CNPJ (14 digits) or CPF (11 digits), digits only. Must be unique across companies",
                example = "11222333000181", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Pattern(regexp = "\\d{11}|\\d{14}", message = "must contain 11 (CPF) or 14 (CNPJ) digits only")
                String taxId,

        @Schema(description = "Contact e-mail", example = "contato@bompao.example.com")
        @Email
        @Size(max = 150)
        String email,

        @Schema(description = "Phone with area code, digits only", example = "11987654321")
        @Size(max = 30)
        String phone
) {
        public CompanyRequest {
                taxId = onlyDigits(taxId);
                phone = onlyDigits(phone);
        }

        private static String onlyDigits(String value) {
                return value == null ? null : value.replaceAll("\\D", "");
        }
}
