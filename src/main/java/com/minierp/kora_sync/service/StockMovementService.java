package com.minierp.kora_sync.service;

import com.minierp.kora_sync.dto.PageResponse;
import com.minierp.kora_sync.dto.StockMovementRequest;
import com.minierp.kora_sync.dto.StockMovementResponse;
import com.minierp.kora_sync.enums.MovementType;
import com.minierp.kora_sync.model.StockMovement;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface StockMovementService {
    StockMovementResponse register(UUID companyId, UUID productId, StockMovementRequest request);
    PageResponse<StockMovementResponse> findAll(UUID companyId, UUID productId, MovementType movementType, Pageable pageable);
}
