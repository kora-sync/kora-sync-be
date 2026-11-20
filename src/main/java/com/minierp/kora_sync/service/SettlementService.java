package com.minierp.kora_sync.service;

import com.minierp.kora_sync.dto.PageResponse;
import com.minierp.kora_sync.dto.SettlementRequest;
import com.minierp.kora_sync.dto.SettlementResponse;
import com.minierp.kora_sync.enums.PaymentMethod;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SettlementService {
    SettlementResponse register(UUID companyId, UUID entryId, SettlementRequest request);
    PageResponse<SettlementResponse> findAll(UUID companyId, UUID entryId, PaymentMethod paymentMethod, Pageable pageable);
}
