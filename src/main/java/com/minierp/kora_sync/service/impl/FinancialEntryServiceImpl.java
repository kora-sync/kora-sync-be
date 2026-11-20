package com.minierp.kora_sync.service.impl;

import com.minierp.kora_sync.dto.FinancialEntryFilter;
import com.minierp.kora_sync.dto.FinancialEntryRequest;
import com.minierp.kora_sync.dto.FinancialEntryResponse;
import com.minierp.kora_sync.dto.PageResponse;
import com.minierp.kora_sync.enums.EntryStatus;
import com.minierp.kora_sync.enums.EntryType;
import com.minierp.kora_sync.exception.BusinessConflictException;
import com.minierp.kora_sync.exception.InvalidInputException;
import com.minierp.kora_sync.exception.ResourceNotFoundException;
import com.minierp.kora_sync.mapper.FinancialEntryMapper;
import com.minierp.kora_sync.model.Customer;
import com.minierp.kora_sync.model.FinancialEntry;
import com.minierp.kora_sync.model.Supplier;
import com.minierp.kora_sync.repository.CompanyRepository;
import com.minierp.kora_sync.repository.CustomerRepository;
import com.minierp.kora_sync.repository.FinancialEntryRepository;
import com.minierp.kora_sync.repository.SupplierRepository;
import com.minierp.kora_sync.service.FinancialEntryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinancialEntryServiceImpl implements FinancialEntryService {
    private final FinancialEntryRepository financialEntryRepository;
    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;
    private final SupplierRepository supplierRepository;
    private final FinancialEntryMapper financialEntryMapper;
    private final Clock clock;

    @Override
    @Transactional
    public FinancialEntryResponse create(UUID companyId, FinancialEntryRequest request) {
        requireCompany(companyId);
        validatePartner(companyId, request);

        FinancialEntry entry = financialEntryMapper.toEntity(request);
        entry.setCompanyId(companyId);

        FinancialEntry saved = financialEntryRepository.save(entry);

        log.info("Financial entry created: id={} companyId={} type={}", saved.getId(), companyId, saved.getEntryType());

        return financialEntryMapper.toResponse(saved, today());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FinancialEntryResponse> findAll(UUID companyId, FinancialEntryFilter filter, Pageable pageable) {
        requireCompany(companyId);

        if (filter.dueFrom() != null && filter.dueTo() != null && filter.dueFrom().isAfter(filter.dueTo())) {
            throw new InvalidInputException("dueFrom must not be after dueTo");
        }

        LocalDate today = today();

        Page<FinancialEntryResponse> page = financialEntryRepository
                .findAll(buildFilters(companyId, filter, today), pageable)
                .map(entry -> financialEntryMapper.toResponse(entry, today));

        return PageResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public FinancialEntryResponse findGetById(UUID companyId, UUID id) {
        return financialEntryMapper.toResponse(findOrThrow(companyId, id), today());
    }

    @Override
    @Transactional
    public FinancialEntryResponse update(UUID companyId, UUID id, FinancialEntryRequest request) {
        FinancialEntry entry = findOrThrow(companyId, id);

        if (!entry.getStatus().isEditable()) {
            throw new BusinessConflictException("Only OPEN entrie can be edied; this one is " + entry.getStatus());
        }

        if (request.entryType() != entry.getEntryType()) {
            throw new InvalidInputException("The entry type cannot be changed");
        }

        validatePartner(companyId, request);

        financialEntryMapper.updateEntity(request, entry);

        FinancialEntry saved = financialEntryRepository.saveAndFlush(entry);

        log.info("Financial entry updated: id={} companyId={}", id, companyId);

        return financialEntryMapper.toResponse(saved, today());
    }

    @Override
    @Transactional
    public void cancel(UUID companyId, UUID id) {
        FinancialEntry entry = findOrThrow(companyId, id);

        if (entry.getStatus().isCancelable()) {
            throw new BusinessConflictException("Only OPEN entries can be canceled; this one is " + entry.getStatus());
        }

        entry.setStatus(EntryStatus.CANCELED);

        log.info("Financial entry canceled: id={} companyId={}", id, companyId);
    }

    private LocalDate today() {
        return LocalDate.now(clock);
    }

    private void requireCompany(UUID companyId) {
        if (!companyRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company", companyId);
        }
    }

    private FinancialEntry findOrThrow(UUID companyId, UUID id) {
        return financialEntryRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Financial entry", id));
    }

    private void validatePartner(UUID companyId, FinancialEntryRequest request) {
        if (request.entryType() == EntryType.RECEIVABLE) {
            if (request.supplierId() != null) {
                throw new InvalidInputException("A receivable cannot reference a supplier");
            }
            if (request.customerId() != null) {
                requireActiveCustomer(companyId, request.customerId());
            }
        } else {
            if (request.customerId() != null) {
                throw new InvalidInputException("A payable cannot reference a customer");
            }
            if (request.supplierId() != null) {
                requireActiveSupplier(companyId, request.supplierId());
            }
        }
    }

    private void requireActiveCustomer(UUID companyId, UUID customerId) {
        Customer customer = customerRepository.findByIdAndCompanyId(customerId, companyId)
                .orElseThrow(() -> new InvalidInputException("Customer not found in this company: " + customerId));
        if (!customer.isActive()) {
            throw new BusinessConflictException("Customer is inactive: " + customerId);
        }
    }

    private void requireActiveSupplier(UUID companyId, UUID supplierId) {
        Supplier supplier = supplierRepository.findByIdAndCompanyId(supplierId, companyId)
                .orElseThrow(() -> new InvalidInputException("Supplier not found in this company: " + supplierId));
        if (!supplier.isActive()) {
            throw new BusinessConflictException("Supplier is inactive: " + supplierId);
        }
    }

    private Specification<FinancialEntry> buildFilters(UUID companyId, FinancialEntryFilter filter, LocalDate today) {
        Specification<FinancialEntry> spec = (root, query, cb) -> cb.equal(root.get("companyId"), companyId);
        if (filter.entryType() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("entryType"), filter.entryType()));
        }
        if (filter.status() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), filter.status()));
        }
        if (filter.customerId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("customerId"), filter.customerId()));
        }
        if (filter.supplierId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("supplierId"), filter.supplierId()));
        }
        if (filter.dueFrom() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.<LocalDate>get("dueDate"), filter.dueFrom()));
        }
        if (filter.dueTo() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.<LocalDate>get("dueDate"), filter.dueTo()));
        }
        if (Boolean.TRUE.equals(filter.overdue())) {
            List<EntryStatus> settleable = Arrays.stream(EntryStatus.values())
                    .filter(EntryStatus::isSettleable)
                    .toList();
            spec = spec.and((root, query, cb) -> cb.and(
                    root.get("status").in(settleable),
                    cb.lessThan(root.<LocalDate>get("dueDate"), today)));
        }
        return spec;
    }
}
