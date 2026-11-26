package com.minierp.kora_sync.mapper;

import com.minierp.kora_sync.dto.NotificationResponse;
import com.minierp.kora_sync.model.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    // "read" comes from Notification.isRead()
    NotificationResponse toResponse(Notification notification);
}