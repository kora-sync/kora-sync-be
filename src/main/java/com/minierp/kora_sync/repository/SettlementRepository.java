package com.minierp.kora_sync.repository;

import com.minierp.kora_sync.dto.DailyFlowTotal;
import com.minierp.kora_sync.dto.PaymentMethodFlowTotal;
import com.minierp.kora_sync.model.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SettlementRepository extends JpaRepository<Settlement, UUID>, JpaSpecificationExecutor<Settlement> {
    @Query("""
        select new com.minierp.kora_sync.dto.DailyFlowTotal(s.settledOn, e.entryType, sum(s.amount))
        from Settlement s join FinancialEntry e on e.id = s.entryId
        where s.companyId = :companyId and e.companyId = :companyId
          and s.settledOn between :startDate and :endDate
        group by s.settledOn, e.entryType
        """)
    List<DailyFlowTotal> sumByDayAndEntryType(@Param("companyId") UUID companyId,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);

    @Query("""
        select new com.minierp.kora_sync.dto.PaymentMethodFlowTotal(s.paymentMethod, e.entryType, sum(s.amount))
        from Settlement s join FinancialEntry e on e.id = s.entryId
        where s.companyId = :companyId and e.companyId = :companyId
          and s.settledOn between :startDate and :endDate
        group by s.paymentMethod, e.entryType
        """)
    List<PaymentMethodFlowTotal> sumByPaymentMethodAndEntryType(@Param("companyId") UUID companyId,
                                                                @Param("startDate") LocalDate startDate,
                                                                @Param("endDate") LocalDate endDate);
}
