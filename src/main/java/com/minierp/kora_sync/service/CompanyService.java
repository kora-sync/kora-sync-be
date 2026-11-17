package com.minierp.kora_sync.service;

import com.minierp.kora_sync.dto.CompanyRequest;
import com.minierp.kora_sync.dto.CompanyResponse;
import com.minierp.kora_sync.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CompanyService {
    CompanyResponse create(CompanyRequest request);
    PageResponse<CompanyResponse> findAll(String name, Boolean active, Pageable pageable);
    CompanyResponse findGetById(UUID id);
    CompanyResponse update(UUID id, CompanyRequest request);
    void deactivate(UUID id);
}
