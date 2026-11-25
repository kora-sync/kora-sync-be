package com.minierp.kora_sync.service.impl;

import com.minierp.kora_sync.dto.PageResponse;
import com.minierp.kora_sync.dto.SettlementRequest;
import com.minierp.kora_sync.dto.SettlementResponse;
import com.minierp.kora_sync.enums.EntryStatus;
import com.minierp.kora_sync.enums.PaymentMethod;
import com.minierp.kora_sync.exception.BusinessConflictException;
import com.minierp.kora_sync.exception.InvalidInputException;
import com.minierp.kora_sync.exception.ResourceNotFoundException;
import com.minierp.kora_sync.mapper.SettlementMapper;
import com.minierp.kora_sync.model.FinancialEntry;
import com.minierp.kora_sync.model.Settlement;
import com.minierp.kora_sync.repository.FinancialEntryRepository;
import com.minierp.kora_sync.repository.SettlementRepository;
import com.minierp.kora_sync.security.CurrentUser;
import com.minierp.kora_sync.service.SettlementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementServiceImpl implements SettlementService {
    private final SettlementRepository settlementRepository;
    private final FinancialEntryRepository financialEntryRepository;
    private final SettlementMapper settlementMapper;
    private final CurrentUser currentUser;
    private final Clock clock;

    @Override
    @Transactional
    public SettlementResponse register(UUID companyId, UUID entryId, SettlementRequest request) {
        FinancialEntry entry = financialEntryRepository.findByIdAndCompanyIdForUpdate(entryId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("financial entry", entryId));

        if (!entry.getStatus().isSettleable()) {
            throw new BusinessConflictException("Entry is " + entry.getStatus() + " and does not accept settlements");
        }

        LocalDate today = LocalDate.now(clock);
        LocalDate settledOn = request.settledOn() == null ? today : request.settledOn();

        if (settledOn.isAfter(today)) {
            throw new InvalidInputException("settledOn cannot be in the future");
        }

        BigDecimal remaining = entry.getRemainingAmount();

        if (request.amount().compareTo(remaining) > 0) {
            throw new BusinessConflictException(
                    "Amount exceeds the remaining balance of the entry: remaining %s, informed %s"
                            .formatted(remaining.toPlainString(), request.amount().toPlainString())
            );
        }

        BigDecimal newSettled = entry.getSettledAmount().add(request.amount());
        entry.setSettledAmount(newSettled);
        entry.setStatus(newSettled.compareTo(entry.getAmount()) == 0
                ? EntryStatus.SETTLED
                : EntryStatus.PARTIALLY_SETTLED);
        Settlement settlement = new Settlement();
        settlement.setCompanyId(companyId);
        settlement.setEntryId(entryId);
        settlement.setAmount(request.amount());
        settlement.setSettledOn(settledOn);
        settlement.setPaymentMethod(request.paymentMethod());
        settlement.setNotes(request.notes());
        settlement.setCreatedBy(currentUser.get().userId());
        Settlement saved = settlementRepository.save(settlement);

        log.info("Settlement registered: id={} entryid={} companyId={} amount={} status={}",
                saved.getId(), entryId, companyId, request.amount(), entry.getStatus());

        return settlementMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SettlementResponse> findAll(UUID companyId, UUID entryId, PaymentMethod paymentMethod, Pageable pageable) {
        if (!financialEntryRepository.existsByIdAndCompanyId(entryId, companyId)) {
            throw new ResourceNotFoundException("Financial entry", entryId);
        }

        Page<SettlementResponse> page = settlementRepository
                .findAll(buildFilters(companyId, entryId, paymentMethod), pageable)
                .map(settlementMapper::toResponse);

        return PageResponse.from(page);
    }

    private Specification<Settlement> buildFilters(UUID companyId, UUID entryId, PaymentMethod paymentMethod) {
        Specification<Settlement> spec = (root, query, cb) -> cb.and(
                cb.equal(root.get("companyId"), companyId),
                cb.equal(root.get("entryId"), entryId));
        if (paymentMethod != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("paymentMethod"), paymentMethod));
        }
        return spec;
    }
}
