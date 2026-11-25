package com.minierp.kora_sync.dto;

import com.minierp.kora_sync.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Data to create a user in the company of the administrator")
public record CreateUserRequest(

        @Schema(description = "User name", example = "Marta Gerente", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Size(max = 150)
        String name,

        @Schema(description = "Login e-mail. Unique in the whole system and stored in lowercase",
                example = "marta@padariakora.com.br", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Email
        @Size(max = 254)
        String email,

        @Schema(description = "Initial password, 8 to 72 characters", example = "Sup3r-secret", format = "password",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Size(min = 8, max = 72)
        String password,

        @Schema(description = "Access level of the user", example = "MANAGER",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        Role role
) {

    public CreateUserRequest {
        name = name == null ? null : name.strip();
        email = InputNormalizer.email(email);
    }
}