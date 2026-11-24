package com.minierp.kora_sync.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Result of a successful sign-in or registration")
public record AuthResponse(

        @Schema(description = "JWT to send in the Authorization header as 'Bearer <token>'",
                example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIwZDZjOGUyYSJ9.4Vq1xk")
        String accessToken,

        @Schema(description = "Token type, always Bearer", example = "Bearer")
        String tokenType,

        @Schema(description = "Seconds until the access token expires", example = "900")
        long expiresIn,

        @Schema(description = "The signed-in user")
        UserResponse user
) {
}