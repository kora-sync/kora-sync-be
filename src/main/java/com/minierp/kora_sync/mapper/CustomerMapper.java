package com.minierp.kora_sync.mapper;

import com.minierp.kora_sync.dto.CustomerRequest;
import com.minierp.kora_sync.dto.CustomerResponse;
import com.minierp.kora_sync.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CustomerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Customer toEntity(CustomerRequest request);

    CustomerResponse toResponse(Customer customer);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(CustomerRequest request, @MappingTarget Customer customer);
}