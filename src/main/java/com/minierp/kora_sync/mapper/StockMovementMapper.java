package com.minierp.kora_sync.mapper;

import com.minierp.kora_sync.dto.StockMovementResponse;
import com.minierp.kora_sync.model.StockMovement;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface StockMovementMapper {

    StockMovementResponse toResponse(StockMovement movement);
}