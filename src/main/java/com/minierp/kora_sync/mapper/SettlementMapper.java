package com.minierp.kora_sync.mapper;

import com.minierp.kora_sync.dto.SettlementResponse;
import com.minierp.kora_sync.model.Settlement;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface SettlementMapper {
    SettlementResponse toResponse(Settlement settlement);
}