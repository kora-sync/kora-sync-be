package com.minierp.kora_sync.controller;

import com.minierp.kora_sync.enums.ExportFormat;
import com.minierp.kora_sync.enums.Role;
import com.minierp.kora_sync.export.ExportResponses;
import com.minierp.kora_sync.security.CurrentCompany;
import com.minierp.kora_sync.security.RequiresAdmin;
import com.minierp.kora_sync.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Reports", description = "Download data as files, streamed row by row")
public class ReportController {

    private final ReportService reportService;
    private final ExportResponses exportResponses;

    // The ResponseEntity defines the status here, so there is no @ResponseStatus on these methods.
    @GetMapping("/users")
    @RequiresAdmin
    @Operation(
            summary = "Export users",
            description = "Downloads the users of the company as a file, with the same filters as the user "
                    + "list. The file is streamed, so it can be large. Texts that start with = + - @ get a "
                    + "leading apostrophe so spreadsheets do not run them as formulas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The file",
                    content = @Content(mediaType = "text/csv", schema = @Schema(type = "string", format = "binary")))
    })
    public ResponseEntity<StreamingResponseBody> users(
            @CurrentCompany UUID companyId,
            @Parameter(description = "File format", example = "CSV")
            @RequestParam(defaultValue = "CSV") ExportFormat format,
            @Parameter(description = "Part of the name, case-insensitive", example = "marta")
            @RequestParam(required = false) String name,
            @Parameter(description = "Only users with this role", example = "MANAGER")
            @RequestParam(required = false) Role role,
            @Parameter(description = "Only active (true) or inactive (false) users", example = "true")
            @RequestParam(required = false) Boolean active) {
        return exportResponses.download("users", format,
                out -> reportService.exportUsers(companyId, name, role, active, format, out));
    }
}