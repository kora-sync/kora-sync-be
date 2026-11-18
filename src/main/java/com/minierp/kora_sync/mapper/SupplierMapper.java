package com.minierp.kora_sync.mapper;

import com.minierp.kora_sync.dto.SupplierRequest;
import com.minierp.kora_sync.dto.SupplierResponse;
import com.minierp.kora_sync.model.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface SupplierMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Supplier toEntity(SupplierRequest request);

    SupplierResponse toResponse(Supplier supplier);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(SupplierRequest request, @MappingTarget Supplier supplier);
}