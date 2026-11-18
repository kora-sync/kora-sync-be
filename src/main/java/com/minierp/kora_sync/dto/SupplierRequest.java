package com.minierp.kora_sync.dto;

import com.minierp.kora_sync.validation.TaxId;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Data required to create or update a supplier")
public record SupplierRequest(

        @Schema(description = "Supplier name or trade name", example = "Distribuidora Pão Dourado",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Size(max = 150)
        String name,

        @Schema(description = "CPF (11 digits) or CNPJ (14 characters), optional. Punctuation is removed "
                + "automatically and the check digits are validated. Must be unique within the company "
                + "when provided",
                example = "11222333000181")
        @TaxId
        String document,

        @Schema(description = "Contact e-mail", example = "vendas@paodourado.example.com")
        @Email
        @Size(max = 150)
        String email,

        @Schema(description = "Phone with area code. Non-digit characters are removed automatically",
                example = "1133334444")
        @Size(max = 30)
        String phone
) {

    public SupplierRequest {
        document = InputNormalizer.taxId(document);
        phone = InputNormalizer.digits(phone);
    }
}