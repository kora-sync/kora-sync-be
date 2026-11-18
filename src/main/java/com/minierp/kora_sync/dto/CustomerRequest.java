package com.minierp.kora_sync.dto;

import com.minierp.kora_sync.validation.TaxId;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Data required to create or update a customer")
public record CustomerRequest(

        @Schema(description = "Customer full name or trade name", example = "Maria Silva",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Size(max = 150)
        String name,

        @Schema(description = "CPF (11 digits) or CNPJ (14 characters), optional. Punctuation is removed "
                + "automatically and the check digits are validated. Must be unique within the company "
                + "when provided",
                example = "52998224725")
        @TaxId
        String document,

        @Schema(description = "Contact e-mail", example = "maria.silva@example.com")
        @Email
        @Size(max = 150)
        String email,

        @Schema(description = "Phone with area code. Non-digit characters are removed automatically",
                example = "11987654321")
        @Size(max = 30)
        String phone
) {

    public CustomerRequest {
        document = InputNormalizer.taxId(document);
        phone = InputNormalizer.digits(phone);
    }
}