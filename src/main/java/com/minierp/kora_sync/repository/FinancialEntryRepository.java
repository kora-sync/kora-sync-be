package com.minierp.kora_sync.repository;

import com.minierp.kora_sync.model.FinancialEntry;
import com.minierp.kora_sync.enums.EntryStatus;
import com.minierp.kora_sync.dto.DailyFlowTotal;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FinancialEntryRepository extends JpaRepository<FinancialEntry, UUID>, JpaSpecificationExecutor<FinancialEntry> {
    Optional<FinancialEntry> findByIdAndCompanyId(UUID id, UUID companyId);

    boolean existsByIdAndCompanyId(UUID id, UUID companyId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from FinancialEntry e where e.id = :id and e.companyId = :companyId")
    Optional<FinancialEntry> findByIdAndCompanyIdForUpdate(@Param("id") UUID id, @Param("companyId") UUID companyId);

    @Query("""
        select new com.minierp.kora_sync.dto.DailyFlowTotal(e.dueDate, e.entryType, sum(e.amount - e.settledAmount))
        from FinancialEntry e
        where e.companyId = :companyId and e.status in :statuses
          and e.dueDate between :startDate and :endDate
        group by e.dueDate, e.entryType
        """)
    List<DailyFlowTotal> sumRemainingByDueDateAndEntryType(@Param("companyId") UUID companyId,
                                                           @Param("statuses") Collection<EntryStatus> statuses,
                                                           @Param("startDate") LocalDate startDate,
                                                           @Param("endDate") LocalDate endDate);
}
