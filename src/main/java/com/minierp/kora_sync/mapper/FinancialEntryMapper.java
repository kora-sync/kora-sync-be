package com.minierp.kora_sync.mapper;

import com.minierp.kora_sync.dto.FinancialEntryRequest;
import com.minierp.kora_sync.dto.FinancialEntryResponse;
import com.minierp.kora_sync.model.FinancialEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDate;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface FinancialEntryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "settledAmount", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    FinancialEntry toEntity(FinancialEntryRequest request);

    @Mapping(target = "overdue", expression = "java(entry.isOverdueOn(today))")
    FinancialEntryResponse toResponse(FinancialEntry entry, LocalDate today);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "entryType", ignore = true)
    @Mapping(target = "settledAmount", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(FinancialEntryRequest request, @MappingTarget FinancialEntry entry);
}