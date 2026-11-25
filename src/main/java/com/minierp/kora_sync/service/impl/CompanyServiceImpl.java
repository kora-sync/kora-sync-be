package com.minierp.kora_sync.service.impl;

import com.minierp.kora_sync.dto.CompanyRequest;
import com.minierp.kora_sync.dto.CompanyResponse;
import com.minierp.kora_sync.dto.PageResponse;
import com.minierp.kora_sync.exception.BusinessConflictException;
import com.minierp.kora_sync.exception.ResourceNotFoundException;
import com.minierp.kora_sync.mapper.CompanyMapper;
import com.minierp.kora_sync.model.Company;
import com.minierp.kora_sync.repository.CompanyRepository;
import com.minierp.kora_sync.service.CompanyService;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    @Override
    @Transactional(readOnly = true)
    public CompanyResponse findGetById(UUID id) {
        return companyMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional
    public CompanyResponse update(UUID id, CompanyRequest request) {
        Company company = findOrThrow(id);
        if (companyRepository.existsByTaxIdAndIdNot(request.taxId(), id)) {
            throw new BusinessConflictException("Tax id already registered: " + request.taxId());
        }
        companyMapper.updateEntity(request, company);
        Company saved = companyRepository.saveAndFlush(company);
        log.info("Company updated: id={}", id);
        return companyMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deactivate(UUID id) {
        Company company = findOrThrow(id);
        company.setActive(false);
        log.info("Company deactivated: id={}", id);
    }

    private Company findOrThrow(UUID id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", id));
    }

    private Specification<Company> buildFilters(String name, Boolean active) {
        Specification<Company> spec = (root, query, cb) -> cb.conjunction();
        if (name != null && !name.isBlank()) {
            String pattern = "%" + name.strip().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), pattern));
        }
        if (active != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("active"), active));
        }
        return spec;
    }
}
