package com.minierp.kora_sync.model;

import com.minierp.kora_sync.enums.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "notifications")
public class Notification extends BaseEntity {

    @Column(name = "company_id", nullable = false, updatable = false)
    private UUID companyId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false, length = 30)
    private NotificationType type;

    @Column(nullable = false, updatable = false, length = 150)
    private String title;

    @Column(nullable = false, updatable = false, length = 500)
    private String message;

    @Column(name = "reference_id", updatable = false)
    private UUID referenceId;

    @Column(name = "dedupe_key", nullable = false, updatable = false, length = 150)
    private String dedupeKey;

    @Column(name = "read_at")
    private Instant readAt;

    @Column(name = "read_by")
    private UUID readBy;

    public boolean isRead() {
        return readAt != null;
    }
}