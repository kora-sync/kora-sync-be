package com.minierp.kora_sync.service;

import com.minierp.kora_sync.dto.MarkAllReadResponse;
import com.minierp.kora_sync.dto.NotificationResponse;
import com.minierp.kora_sync.dto.PageResponse;
import com.minierp.kora_sync.dto.UnreadCountResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationService {
    PageResponse<NotificationResponse> findAll(UUID companyId, Boolean unread, Pageable pageable);
    UnreadCountResponse countUnread(UUID companyId);
    NotificationResponse markAsRead(UUID companyId, UUID id);
    MarkAllReadResponse markAllAsRead(UUID companyId);
}
