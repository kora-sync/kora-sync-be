package com.minierp.kora_sync.dto;

import com.minierp.kora_sync.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "New name and role of a user. The e-mail cannot be changed")
public record UpdateUserRequest(

        @Schema(description = "User name", example = "Marta Souza", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Size(max = 150)
        String name,

        @Schema(description = "Access level of the user", example = "MANAGER",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        Role role
) {

    public UpdateUserRequest {
        name = name == null ? null : name.strip();
    }
}