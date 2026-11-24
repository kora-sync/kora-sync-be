package com.minierp.kora_sync.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Data to register a new company together with its first user, who becomes the ADMIN")
public record RegisterRequest(

        @Schema(description = "The company being registered", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Valid
        CompanyRequest company,

        @Schema(description = "Name of the first user, the administrator", example = "Ana Souza",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Size(max = 150)
        String adminName,

        @Schema(description = "Login e-mail of the administrator. Unique in the whole system and stored in lowercase",
                example = "ana@padariakora.com.br", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Email
        @Size(max = 254)
        String adminEmail,

        @Schema(description = "Password, 8 to 72 characters", example = "Sup3r-secret", format = "password",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Size(min = 8, max = 72)
        String adminPassword
) {

    public RegisterRequest {
        adminName = adminName == null ? null : adminName.strip();
        adminEmail = InputNormalizer.email(adminEmail);
    }
}