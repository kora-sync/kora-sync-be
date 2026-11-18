package com.minierp.kora_sync.controller;

import com.minierp.kora_sync.dto.CustomerRequest;
import com.minierp.kora_sync.dto.CustomerResponse;
import com.minierp.kora_sync.dto.PageResponse;
import com.minierp.kora_sync.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/companies/{companyId}/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Register and manage the customers of a company")

public class CustomerController {
    private final CustomerService customerService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a customer",
            description = "Creates a customer for the given company. The document (CPF/CNPJ), when informed, "
                    + "must be unique within the company. New customers are created active.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Customer created"),
            @ApiResponse(responseCode = "404", description = "Company not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Document already registered for this company",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public CustomerResponse create(
            @Parameter(description = "Company identifier", example = "cfbf7095-6ec9-40da-9fac-8570b746de4d")
            @PathVariable UUID companyId,
            @Valid @RequestBody CustomerRequest request) {
        return customerService.create(companyId, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "List customers",
            description = "Returns a page of customers of the company. All filters are optional and can be "
                    + "combined. Use page (zero-based), size (max 100) and sort (e.g. name,asc). "
                    + "When no sort is given, results are ordered by name.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of customers (empty when nothing matches)"),
            @ApiResponse(responseCode = "404", description = "Company not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public PageResponse<CustomerResponse> findAll(
            @Parameter(description = "Company identifier", example = "cfbf7095-6ec9-40da-9fac-8570b746de4d")
            @PathVariable UUID companyId,
            @Parameter(description = "Case-insensitive partial match on the customer name", example = "maria")
            @RequestParam(required = false) String name,
            @Parameter(description = "Filter by active flag. Omit to return both active and inactive customers",
                    example = "true")
            @RequestParam(required = false) Boolean active,
            @ParameterObject @SortDefault(sort = "name") Pageable pageable) {
        return customerService.findAll(companyId, name, active, pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get a customer",
            description = "Returns the customer with the given identifier, active or not. "
                    + "Customers of other companies are never returned.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "404", description = "Customer not found in this company",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public CustomerResponse findGetById(
            @Parameter(description = "Company identifier", example = "cfbf7095-6ec9-40da-9fac-8570b746de4d")
            @PathVariable UUID companyId,
            @Parameter(description = "Customer identifier", example = "7c9e6679-7425-40de-944b-e07fc1f90ae7")
            @PathVariable UUID id) {
        return customerService.findGetById(companyId, id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Update a customer",
            description = "Replaces all editable fields of the customer. The document, when informed, must remain "
                    + "unique within the company. The active flag is not changed by this operation.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer updated"),
            @ApiResponse(responseCode = "404", description = "Customer not found in this company",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Document already registered for this company",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public CustomerResponse update(
            @Parameter(description = "Company identifier", example = "cfbf7095-6ec9-40da-9fac-8570b746de4d")
            @PathVariable UUID companyId,
            @Parameter(description = "Customer identifier", example = "7c9e6679-7425-40de-944b-e07fc1f90ae7")
            @PathVariable UUID id,
            @Valid @RequestBody CustomerRequest request) {
        return customerService.update(companyId, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Deactivate a customer",
            description = "Marks the customer as inactive. The record and its history are kept, so the customer "
                    + "is not physically deleted. Deactivating an already inactive customer has no effect.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Customer deactivated"),
            @ApiResponse(responseCode = "404", description = "Customer not found in this company",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public void deactivate(
            @Parameter(description = "Company identifier", example = "cfbf7095-6ec9-40da-9fac-8570b746de4d")
            @PathVariable UUID companyId,
            @Parameter(description = "Customer identifier", example = "7c9e6679-7425-40de-944b-e07fc1f90ae7")
            @PathVariable UUID id) {
        customerService.deactivate(companyId, id);
    }
}
