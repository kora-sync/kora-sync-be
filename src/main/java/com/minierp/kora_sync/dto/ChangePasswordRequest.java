package com.minierp.kora_sync.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Current and new password of the signed-in user")
public record ChangePasswordRequest(

        @Schema(description = "Current password", example = "Sup3r-secret", format = "password",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String currentPassword,

        @Schema(description = "New password, 8 to 72 characters, different from the current one",
                example = "N3w-pass-9999", format = "password", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Size(min = 8, max = 72)
        String newPassword
) {
}