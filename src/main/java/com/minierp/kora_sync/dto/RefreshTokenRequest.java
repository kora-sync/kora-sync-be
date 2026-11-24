package com.minierp.kora_sync.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "A refresh token, used to renew the access token or to sign out")
public record RefreshTokenRequest(

        @Schema(description = "Refresh token received at sign-in or in the last refresh",
                example = "q3WbX0m1H5kTn2cJ8yZpR4vLdE6sA9uFgO7iKtNhBxM",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Size(max = 200)
        String refreshToken
) {

    public RefreshTokenRequest {
        refreshToken = refreshToken == null ? null : refreshToken.strip();
    }
}