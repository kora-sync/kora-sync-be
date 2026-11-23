package com.minierp.kora_sync.service.impl;

import com.minierp.kora_sync.dto.CashFlowDayResponse;
import com.minierp.kora_sync.dto.DailyFlowTotal;
import com.minierp.kora_sync.dto.ForecastCashFlowResponse;
import com.minierp.kora_sync.dto.PaymentMethodFlowResponse;
import com.minierp.kora_sync.dto.PaymentMethodFlowTotal;
import com.minierp.kora_sync.dto.RealizedCashFlowResponse;
import com.minierp.kora_sync.enums.EntryStatus;
import com.minierp.kora_sync.enums.EntryType;
import com.minierp.kora_sync.enums.PaymentMethod;
import com.minierp.kora_sync.exception.InvalidInputException;
import com.minierp.kora_sync.exception.ResourceNotFoundException;
import com.minierp.kora_sync.repository.CompanyRepository;
import com.minierp.kora_sync.repository.FinancialEntryRepository;
import com.minierp.kora_sync.repository.SettlementRepository;
import com.minierp.kora_sync.service.CashFlowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CashFlowServiceImpl implements CashFlowService {

    private static final int MAX_PERIOD_DAYS = 366;

    private final SettlementRepository settlementRepository;
    private final FinancialEntryRepository financialEntryRepository;
    private final CompanyRepository companyRepository;

    @Override
    @Transactional(readOnly = true)
    public RealizedCashFlowResponse realized(UUID companyId, LocalDate from, LocalDate to) {
        validate(companyId, from, to);
        DailySeries series = DailySeries.of(from, to,
                settlementRepository.sumByDayAndEntryType(companyId, from, to));
        List<PaymentMethodFlowResponse> byPaymentMethod = byPaymentMethod(
                settlementRepository.sumByPaymentMethodAndEntryType(companyId, from, to));
        return new RealizedCashFlowResponse(from, to, series.totalInflow(), series.totalOutflow(),
                series.net(), series.days(), byPaymentMethod);
    }

    @Override
    @Transactional(readOnly = true)
    public ForecastCashFlowResponse forecast(UUID companyId, LocalDate from, LocalDate to) {
        validate(companyId, from, to);
        DailySeries series = DailySeries.of(from, to,
                financialEntryRepository.sumRemainingByDueDateAndEntryType(
                        companyId, EntryStatus.settleableStatuses(), from, to));
        return new ForecastCashFlowResponse(from, to, series.totalInflow(), series.totalOutflow(),
                series.net(), series.days());
    }

    private void validate(UUID companyId, LocalDate from, LocalDate to) {
        if (!companyRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company", companyId);
        }
        if (from.isAfter(to)) {
            throw new InvalidInputException("'from' must not be after 'to'");
        }
        if (ChronoUnit.DAYS.between(from, to) >= MAX_PERIOD_DAYS) {
            throw new InvalidInputException("The period cannot exceed %d days".formatted(MAX_PERIOD_DAYS));
        }
    }

    private List<PaymentMethodFlowResponse> byPaymentMethod(List<PaymentMethodFlowTotal> totals) {
        return Arrays.stream(PaymentMethod.values())
                .map(method -> new PaymentMethodFlowResponse(
                        method,
                        amountOf(totals, method, EntryType.RECEIVABLE),
                        amountOf(totals, method, EntryType.PAYABLE)))
                .filter(flow -> flow.inflow().signum() > 0 || flow.outflow().signum() > 0)
                .toList();
    }

    private BigDecimal amountOf(List<PaymentMethodFlowTotal> totals, PaymentMethod method, EntryType type) {
        return totals.stream()
                .filter(total -> total.paymentMethod() == method && total.entryType() == type)
                .map(PaymentMethodFlowTotal::total)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private record DailySeries(List<CashFlowDayResponse> days, BigDecimal totalInflow, BigDecimal totalOutflow) {

        static DailySeries of(LocalDate from, LocalDate to, List<DailyFlowTotal> totals) {
            Map<LocalDate, BigDecimal> inflow = amountsByDay(totals, EntryType.RECEIVABLE);
            Map<LocalDate, BigDecimal> outflow = amountsByDay(totals, EntryType.PAYABLE);

            List<CashFlowDayResponse> days = new ArrayList<>();
            BigDecimal cumulative = BigDecimal.ZERO;
            for (LocalDate date : from.datesUntil(to.plusDays(1)).toList()) {
                BigDecimal in = inflow.getOrDefault(date, BigDecimal.ZERO);
                BigDecimal out = outflow.getOrDefault(date, BigDecimal.ZERO);
                BigDecimal net = in.subtract(out);
                cumulative = cumulative.add(net);
                days.add(new CashFlowDayResponse(date, in, out, net, cumulative));
            }
            return new DailySeries(List.copyOf(days), sum(inflow), sum(outflow));
        }

        BigDecimal net() {
            return totalInflow.subtract(totalOutflow);
        }

        private static Map<LocalDate, BigDecimal> amountsByDay(List<DailyFlowTotal> totals, EntryType type) {
            return totals.stream()
                    .filter(total -> total.entryType() == type)
                    .collect(Collectors.toMap(DailyFlowTotal::day, DailyFlowTotal::total));
        }

        private static BigDecimal sum(Map<LocalDate, BigDecimal> amounts) {
            return amounts.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }
}