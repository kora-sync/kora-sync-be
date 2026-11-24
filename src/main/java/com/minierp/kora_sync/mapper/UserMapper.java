package com.minierp.kora_sync.mapper;

import com.minierp.kora_sync.dto.UserResponse;
import com.minierp.kora_sync.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {
    UserResponse toResponse(User user);
}
