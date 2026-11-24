package com.minierp.kora_sync.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Result of a successful sign-in, registration or token refresh")
public record AuthResponse(

        @Schema(description = "JWT to send in the Authorization header as 'Bearer <token>'",
                example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIwZDZjOGUyYSJ9.4Vq1xk")
        String accessToken,

        @Schema(description = "Token type, always Bearer", example = "Bearer")
        String tokenType,

        @Schema(description = "Seconds until the access token expires", example = "900")
        long expiresIn,

        @Schema(description = "Opaque token used only to get a new access token. Single use: every refresh "
                + "returns a new one", example = "q3WbX0m1H5kTn2cJ8yZpR4vLdE6sA9uFgO7iKtNhBxM")
        String refreshToken,

        @Schema(description = "Seconds until the refresh token expires", example = "604800")
        long refreshExpiresIn,

        @Schema(description = "The signed-in user")
        UserResponse user
) {
}