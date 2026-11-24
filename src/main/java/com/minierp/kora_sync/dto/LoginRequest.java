package com.minierp.kora_sync.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credentials to sign in")
public record LoginRequest(
        @Schema(description = "Login e-mail; case and surrounding spaces are ignored",
                example = "ana@padariakora.com.br", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String email,

        @Schema(description = "Password", example = "Sup3r-secret", format = "password",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String password
) {
    public LoginRequest {
        email = InputNormalizer.email(email);
    }
}
