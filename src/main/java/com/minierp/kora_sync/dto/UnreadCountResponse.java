package com.minierp.kora_sync.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "How many notifications are still unread")
public record UnreadCountResponse(

        @Schema(description = "Number of unread notifications", example = "3")
        long unread
) {
}