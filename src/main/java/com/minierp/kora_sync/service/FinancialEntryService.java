package com.minierp.kora_sync.service;

import com.minierp.kora_sync.dto.FinancialEntryFilter;
import com.minierp.kora_sync.dto.FinancialEntryRequest;
import com.minierp.kora_sync.dto.FinancialEntryResponse;
import com.minierp.kora_sync.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface FinancialEntryService {
    FinancialEntryResponse create(UUID companyId, FinancialEntryRequest request);
    PageResponse<FinancialEntryResponse> findAll(UUID companyId, FinancialEntryFilter filter, Pageable pageable);
    FinancialEntryResponse findGetById(UUID companyId, UUID id);
    FinancialEntryResponse update(UUID companyId, UUID id, FinancialEntryRequest request);
    void cancel(UUID companyId, UUID id);
}
