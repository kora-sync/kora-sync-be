package com.minierp.kora_sync.service.impl;

import com.minierp.kora_sync.dto.PageResponse;
import com.minierp.kora_sync.dto.SupplierRequest;
import com.minierp.kora_sync.dto.SupplierResponse;
import com.minierp.kora_sync.exception.BusinessConflictException;
import com.minierp.kora_sync.exception.ResourceNotFoundException;
import com.minierp.kora_sync.mapper.SupplierMapper;
import com.minierp.kora_sync.model.Supplier;
import com.minierp.kora_sync.repository.CompanyRepository;
import com.minierp.kora_sync.repository.SupplierRepository;
import com.minierp.kora_sync.service.SupplierService;
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
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepository supplierRepository;
    private final CompanyRepository companyRepository;
    private final SupplierMapper supplierMapper;

    @Override
    @Transactional
    public SupplierResponse create(UUID companyId, SupplierRequest request) {
        requireCompany(companyId);

        if (request.document() != null && supplierRepository.existsByCompanyIdAndDocument(companyId, request.document())) {
            throw new BusinessConflictException("Document already registered for company: " + request.document());
        }

        Supplier supplier = supplierMapper.toEntity(request);

        supplier.setCompanyId(companyId);

        Supplier saved = supplierRepository.save(supplier);

        log.info("Supplier created: id={} companyId={}", saved.getId(), companyId);

        return supplierMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SupplierResponse> findAll(UUID companyId, String name, Boolean active, Pageable pageable) {
        requireCompany(companyId);

        Page<SupplierResponse> page = supplierRepository
                .findAll(buildFilters(companyId, name, active), pageable)
                .map(supplierMapper::toResponse);

        return PageResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierResponse findGetById(UUID companyId, UUID id) {
        return supplierMapper.toResponse(findOrThrow(companyId, id));
    }

    @Override
    @Transactional
    public SupplierResponse update(UUID companyId, UUID id, SupplierRequest request) {
        Supplier supplier = findOrThrow(companyId, id);

        if (request.document() != null && supplierRepository.existsByCompanyIdAndDocumentAndIdNot(companyId, request.document(), id)) {
            throw new BusinessConflictException("Document already for this company: " + request.document());
        }

        supplierMapper.updateEntity(request, supplier);

        Supplier saved = supplierRepository.saveAndFlush(supplier);

        log.info("Supplier updated: id={} companyId={}", id, companyId);

        return supplierMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deactivate(UUID companyId, UUID id) {
        Supplier supplier = findOrThrow(companyId, id);

        supplier.setActive(false);

        log.info("Supplier deactivated: id={} companyId={}", id, companyId);
    }

    private void requireCompany(UUID companyId) {
        if (!companyRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company", companyId);
        }
    }

    private Supplier findOrThrow(UUID companyId, UUID id) {
        return supplierRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", id));
    }

    private Specification<Supplier> buildFilters(UUID companyId, String name, Boolean active) {
        Specification<Supplier> spec = (root, query, cb) -> cb.equal(root.get("companyId"), companyId);
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
