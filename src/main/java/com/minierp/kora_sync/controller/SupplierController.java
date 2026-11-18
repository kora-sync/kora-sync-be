package com.minierp.kora_sync.controller;

import com.minierp.kora_sync.dto.PageResponse;
import com.minierp.kora_sync.dto.SupplierRequest;
import com.minierp.kora_sync.dto.SupplierResponse;
import com.minierp.kora_sync.service.SupplierService;
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
@RequestMapping("/api/companies/{companyId}/suppliers")
@RequiredArgsConstructor
@Tag(name = "Suppliers", description = "Register and manage the suppliers of a company")
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a supplier",
            description = "Creates a supplier for the given company. The document (CPF/CNPJ), when informed, "
                    + "must be unique within the company. New suppliers are created active.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Supplier created"),
            @ApiResponse(responseCode = "404", description = "Company not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Document already registered for this company",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public SupplierResponse create(
            @Parameter(description = "Company identifier", example = "cfbf7095-6ec9-40da-9fac-8570b746de4d")
            @PathVariable UUID companyId,
            @Valid @RequestBody SupplierRequest request) {
        return supplierService.create(companyId, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "List suppliers",
            description = "Returns a page of suppliers of the company. All filters are optional and can be "
                    + "combined. Use page (zero-based), size (max 100) and sort (e.g. name,asc). "
                    + "When no sort is given, results are ordered by name.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of suppliers (empty when nothing matches)"),
            @ApiResponse(responseCode = "404", description = "Company not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public PageResponse<SupplierResponse> list(
            @Parameter(description = "Company identifier", example = "cfbf7095-6ec9-40da-9fac-8570b746de4d")
            @PathVariable UUID companyId,
            @Parameter(description = "Case-insensitive partial match on the supplier name", example = "pão")
            @RequestParam(required = false) String name,
            @Parameter(description = "Filter by active flag. Omit to return both active and inactive suppliers",
                    example = "true")
            @RequestParam(required = false) Boolean active,
            @ParameterObject @SortDefault(sort = "name") Pageable pageable) {
        return supplierService.findAll(companyId, name, active, pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get a supplier",
            description = "Returns the supplier with the given identifier, active or not. "
                    + "Suppliers of other companies are never returned.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Supplier found"),
            @ApiResponse(responseCode = "404", description = "Supplier not found in this company",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public SupplierResponse getById(
            @Parameter(description = "Company identifier", example = "cfbf7095-6ec9-40da-9fac-8570b746de4d")
            @PathVariable UUID companyId,
            @Parameter(description = "Supplier identifier", example = "9b2f4c1e-3a7d-4f60-8e55-2d1c7a9b6f03")
            @PathVariable UUID id) {
        return supplierService.findGetById(companyId, id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Update a supplier",
            description = "Replaces all editable fields of the supplier. The document, when informed, must remain "
                    + "unique within the company. The active flag is not changed by this operation.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Supplier updated"),
            @ApiResponse(responseCode = "404", description = "Supplier not found in this company",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Document already registered for this company",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public SupplierResponse update(
            @Parameter(description = "Company identifier", example = "cfbf7095-6ec9-40da-9fac-8570b746de4d")
            @PathVariable UUID companyId,
            @Parameter(description = "Supplier identifier", example = "9b2f4c1e-3a7d-4f60-8e55-2d1c7a9b6f03")
            @PathVariable UUID id,
            @Valid @RequestBody SupplierRequest request) {
        return supplierService.update(companyId, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Deactivate a supplier",
            description = "Marks the supplier as inactive. The record and its history are kept, so the supplier "
                    + "is not physically deleted. Deactivating an already inactive supplier has no effect.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Supplier deactivated"),
            @ApiResponse(responseCode = "404", description = "Supplier not found in this company",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public void deactivate(
            @Parameter(description = "Company identifier", example = "cfbf7095-6ec9-40da-9fac-8570b746de4d")
            @PathVariable UUID companyId,
            @Parameter(description = "Supplier identifier", example = "9b2f4c1e-3a7d-4f60-8e55-2d1c7a9b6f03")
            @PathVariable UUID id) {
        supplierService.deactivate(companyId, id);
    }
}