package com.minierp.kora_sync.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Result of marking all notifications as read")
public record MarkAllReadResponse(

        @Schema(description = "How many notifications were marked as read now", example = "2")
        int updated
) {
}