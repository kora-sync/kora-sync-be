package com.minierp.kora_sync.controller;

import com.minierp.kora_sync.dto.CompanyRequest;
import com.minierp.kora_sync.dto.CompanyResponse;
import com.minierp.kora_sync.dto.PageResponse;
import com.minierp.kora_sync.service.CompanyService;
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
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@Tag(name = "Companies", description = "Register and manage the companies that use Kora Sync")
public class CompanyController {
    private final CompanyService companyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a company",
            description = "Creates a company. The tax id (CPF/CNPJ, digits only) must be unique across all companies. "
                    + "New companies are created active.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Company created"),
            @ApiResponse(responseCode = "409", description = "Tax id already registered",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public CompanyResponse create(@Valid @RequestBody CompanyRequest request) {
        return companyService.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "List companies",
            description = "Returns a page of companies. All filters are optional and can be combined. "
                    + "Use page (zero-based), size (max 100) and sort (e.g. name,asc). "
                    + "When no sort is given, results are ordered by name.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of companies (empty when nothing matches)")
    })
    public PageResponse<CompanyResponse> list(
            @Parameter(description = "Case-insensitive partial match on the company name", example = "bom pão")
            @RequestParam(required = false) String name,
            @Parameter(description = "Filter by active flag. Omit to return both active and inactive companies",
                    example = "true")
            @RequestParam(required = false) Boolean active,
            @ParameterObject @SortDefault(sort = "name") Pageable pageable) {
        return companyService.findAll(name, active, pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get a company",
            description = "Returns the company with the given identifier, whether it is active or not.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Company found"),
            @ApiResponse(responseCode = "404", description = "Company not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public CompanyResponse findGetById(
            @Parameter(description = "Company identifier", example = "cfbf7095-6ec9-40da-9fac-8570b746de4d")
            @PathVariable UUID id) {
        return companyService.findGetById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Update a company",
            description = "Replaces all editable fields of the company. The tax id must remain unique across "
                    + "companies. The active flag is not changed by this operation.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Company updated"),
            @ApiResponse(responseCode = "404", description = "Company not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Tax id already registered by another company",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public CompanyResponse update(
            @Parameter(description = "Company identifier", example = "cfbf7095-6ec9-40da-9fac-8570b746de4d")
            @PathVariable UUID id,
            @Valid @RequestBody CompanyRequest request) {
        return companyService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Deactivate a company",
            description = "Marks the company as inactive. The record and its history are kept, so the company "
                    + "is not physically deleted. Deactivating an already inactive company has no effect.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Company deactivated"),
            @ApiResponse(responseCode = "404", description = "Company not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public void deactivate(
            @Parameter(description = "Company identifier", example = "cfbf7095-6ec9-40da-9fac-8570b746de4d")
            @PathVariable UUID id) {
        companyService.deactivate(id);
    }
}
