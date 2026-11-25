package com.minierp.kora_sync.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "New password for a user, set by an administrator")
public record ResetPasswordRequest(

        @Schema(description = "New password, 8 to 72 characters", example = "N3w-secret-pass", format = "password",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Size(min = 8, max = 72)
        String password
) {
}