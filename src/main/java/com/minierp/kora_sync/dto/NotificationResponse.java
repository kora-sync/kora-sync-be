package com.minierp.kora_sync.dto;

import com.minierp.kora_sync.enums.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "A notification generated for the company")
public record NotificationResponse(

        @Schema(description = "Notification identifier", example = "5f3a9c1e-7b2d-4e86-a4c0-2d8e6b1f9a73")
        UUID id,

        @Schema(description = "Kind of notification", example = "LOW_STOCK")
        NotificationType type,

        @Schema(description = "Short title", example = "Low stock: Arroz")
        String title,

        @Schema(description = "Details", example = "Arroz is at 2.000 KG (minimum 5.000)")
        String message,

        @Schema(description = "Product or financial entry the notification is about, to open the right screen",
                example = "4e8b7d1a-6c2f-4a39-b5d0-91f3a7c2e864", nullable = true)
        UUID referenceId,

        @Schema(description = "Whether someone already marked it as read", example = "false")
        boolean read,

        @Schema(description = "When it was marked as read, in UTC", example = "2026-11-25T18:40:00.000Z", nullable = true)
        Instant readAt,

        @Schema(description = "Creation instant, in UTC", example = "2026-11-25T11:00:00.000Z")
        Instant createdAt
) {
}