package com.minierp.kora_sync.controller;

import com.minierp.kora_sync.dto.FinancialEntryFilter;
import com.minierp.kora_sync.dto.FinancialEntryRequest;
import com.minierp.kora_sync.dto.FinancialEntryResponse;
import com.minierp.kora_sync.dto.PageResponse;
import com.minierp.kora_sync.enums.EntryStatus;
import com.minierp.kora_sync.enums.EntryType;
import com.minierp.kora_sync.security.CurrentCompany;
import com.minierp.kora_sync.service.FinancialEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/financial-entries")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Tag(name = "Financial Entries",
        description = "Register and follow the amounts a company will receive (receivables) or pay (payables)")
public class FinancialEntryController {

    private final FinancialEntryService financialEntryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a financial entry",
            description = "Creates a receivable or a payable with a single due date. New entries start OPEN with "
                    + "nothing settled. A receivable may reference an active customer and a payable an active "
                    + "supplier; the other kind of partner is not allowed. A past due date is accepted.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Entry created"),
            @ApiResponse(responseCode = "404", description = "Company not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "The referenced customer or supplier is inactive",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public FinancialEntryResponse create(
            @CurrentCompany UUID companyId,
            @Valid @RequestBody FinancialEntryRequest request) {
        return financialEntryService.create(companyId, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "List financial entries",
            description = "Returns a page of entries of the company. All filters are optional and can be combined. "
                    + "Use page (zero-based), size (max 100) and sort (e.g. dueDate,desc). "
                    + "When no sort is given, results are ordered by due date, earliest first.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of entries (empty when nothing matches)"),
            @ApiResponse(responseCode = "404", description = "Company not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public PageResponse<FinancialEntryResponse> list(
            @CurrentCompany UUID companyId,
            @Parameter(description = "Only receivables or only payables", example = "RECEIVABLE")
            @RequestParam(required = false) EntryType entryType,
            @Parameter(description = "Only entries in this status", example = "OPEN")
            @RequestParam(required = false) EntryStatus status,
            @Parameter(description = "Only entries of this customer",
                    example = "7c1e9a52-3b4d-4f60-8a2e-d5b9c0f1e377")
            @RequestParam(required = false) UUID customerId,
            @Parameter(description = "Only entries of this supplier",
                    example = "9d3f2b84-5e6a-4c71-b0d8-a1c4e7f29b56")
            @RequestParam(required = false) UUID supplierId,
            @Parameter(description = "Due date from (inclusive), yyyy-MM-dd", example = "2026-12-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueFrom,
            @Parameter(description = "Due date until (inclusive), yyyy-MM-dd", example = "2026-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueTo,
            @Parameter(description = "When true, returns only entries that still accept settlements and are past "
                    + "their due date. When false or omitted, this filter is not applied", example = "true")
            @RequestParam(required = false) Boolean overdue,
            @ParameterObject @SortDefault(sort = "dueDate") Pageable pageable) {
        return financialEntryService.findAll(companyId,
                new FinancialEntryFilter(entryType, status, customerId, supplierId, dueFrom, dueTo, overdue),
                pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get a financial entry",
            description = "Returns the entry with the given identifier, in any status. "
                    + "Entries of other companies are never returned.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entry found"),
            @ApiResponse(responseCode = "404", description = "Entry not found in this company",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public FinancialEntryResponse getById(
            @CurrentCompany UUID companyId,
            @Parameter(description = "Entry identifier", example = "e2a4c6d8-1f3b-4957-8c0e-6b8d0a2f4e91")
            @PathVariable UUID id) {
        return financialEntryService.findGetById(companyId, id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Update a financial entry",
            description = "Replaces the editable fields of an OPEN entry. The entry type cannot be changed. "
                    + "Entries that already have settlements, or are settled or canceled, cannot be edited.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entry updated"),
            @ApiResponse(responseCode = "404", description = "Entry not found in this company",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "The entry is not OPEN, or the referenced "
                    + "customer or supplier is inactive",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public FinancialEntryResponse update(
            @CurrentCompany UUID companyId,
            @Parameter(description = "Entry identifier", example = "e2a4c6d8-1f3b-4957-8c0e-6b8d0a2f4e91")
            @PathVariable UUID id,
            @Valid @RequestBody FinancialEntryRequest request) {
        return financialEntryService.update(companyId, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Cancel a financial entry",
            description = "Marks an OPEN entry as CANCELED. The record is kept for history. Canceling an entry "
                    + "that is already canceled has no effect. Entries with settlements cannot be canceled.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Entry canceled"),
            @ApiResponse(responseCode = "404", description = "Entry not found in this company",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "The entry already has settlements",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public void cancel(
            @CurrentCompany UUID companyId,
            @Parameter(description = "Entry identifier", example = "e2a4c6d8-1f3b-4957-8c0e-6b8d0a2f4e91")
            @PathVariable UUID id) {
        financialEntryService.cancel(companyId, id);
    }
}