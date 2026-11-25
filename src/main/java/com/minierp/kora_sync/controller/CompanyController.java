package com.minierp.kora_sync.controller;

import com.minierp.kora_sync.dto.CompanyRequest;
import com.minierp.kora_sync.dto.CompanyResponse;
import com.minierp.kora_sync.security.CurrentCompany;
import com.minierp.kora_sync.security.RequiresAdmin;
import com.minierp.kora_sync.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Company", description = "The company of the signed-in user. New companies are created by registration")
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get my company",
            description = "Returns the company of the signed-in user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The company")
    })
    public CompanyResponse get(@CurrentCompany UUID companyId) {
        return companyService.findGetById(companyId);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @RequiresAdmin
    @Operation(
            summary = "Update my company",
            description = "Replaces the data of the signed-in user's company. "
                    + "The tax id must not belong to another company.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Company updated"),
            @ApiResponse(responseCode = "409", description = "Tax id already registered by another company",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public CompanyResponse update(@CurrentCompany UUID companyId, @Valid @RequestBody CompanyRequest request) {
        return companyService.update(companyId, request);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequiresAdmin
    @Operation(
            summary = "Deactivate my company",
            description = "Deactivates the company of the signed-in user. Its users can no longer sign in "
                    + "or renew their session. Data is kept. Access tokens already issued stay valid "
                    + "until they expire.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Company deactivated")
    })
    public void deactivate(@CurrentCompany UUID companyId) {
        companyService.deactivate(companyId);
    }
}