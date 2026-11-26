package com.minierp.kora_sync.service.impl;

import com.minierp.kora_sync.dto.MarkAllReadResponse;
import com.minierp.kora_sync.dto.NotificationResponse;
import com.minierp.kora_sync.dto.PageResponse;
import com.minierp.kora_sync.dto.UnreadCountResponse;
import com.minierp.kora_sync.exception.ResourceNotFoundException;
import com.minierp.kora_sync.mapper.NotificationMapper;
import com.minierp.kora_sync.model.Notification;
import com.minierp.kora_sync.repository.NotificationRepository;
import com.minierp.kora_sync.security.CurrentUser;
import com.minierp.kora_sync.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final CurrentUser currentUser;
    private final Clock clock;

    @Override
    public PageResponse<NotificationResponse> findAll(UUID companyId, Boolean unread, Pageable pageable) {
        // The order is fixed (newest first), so a sort sent by the client is ignored.
        Pageable newestFirst = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
        Sort.by(Sort.Direction.DESC, "createdAt", "id"));
        Page<Notification> page = Boolean.TRUE.equals(unread)
                ? notificationRepository.findByCompanyIdAndReadAtIsNull(companyId, newestFirst)
                : notificationRepository.findByCompanyId(companyId, newestFirst);

        return PageResponse.from(page.map(notificationMapper::toResponse));
    }

    @Override
    public UnreadCountResponse countUnread(UUID companyId) {
        return new UnreadCountResponse(notificationRepository.countByCompanyIdAndReadAtIsNull(companyId));
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(UUID companyId, UUID id) {
        Notification notification = notificationRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found", id));

        if (!notification.isRead()) {
            notification.setReadAt(Instant.now(clock));
            notification.setReadBy(currentUser.get().userId());
        }

        return notificationMapper.toResponse(notification);
    }

    @Override
    @Transactional
    public MarkAllReadResponse markAllAsRead(UUID companyId) {
        int updated = notificationRepository.markAllRead(companyId, currentUser.get().userId(), Instant.now(clock));
        return new MarkAllReadResponse(updated);
    }
}
