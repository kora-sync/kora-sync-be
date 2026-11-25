package com.minierp.kora_sync.controller;

import com.minierp.kora_sync.dto.CreateUserRequest;
import com.minierp.kora_sync.dto.PageResponse;
import com.minierp.kora_sync.dto.ResetPasswordRequest;
import com.minierp.kora_sync.dto.UpdateUserRequest;
import com.minierp.kora_sync.dto.UserResponse;
import com.minierp.kora_sync.enums.Role;
import com.minierp.kora_sync.security.CurrentCompany;
import com.minierp.kora_sync.security.RequiresAdmin;
import com.minierp.kora_sync.service.UserService;
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
import org.springframework.data.web.PageableDefault;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
@RequiresAdmin
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users", description = "Manage the users of the company. Administrators only")
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a user",
            description = "Creates a user in the company of the signed-in administrator, with the given role "
                    + "and initial password. The e-mail is unique in the whole system.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "409", description = "E-mail already registered",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public UserResponse create(@CurrentCompany UUID companyId, @Valid @RequestBody CreateUserRequest request) {
        return userService.create(companyId, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "List users",
            description = "Lists the users of the company, paginated, sorted by name by default. "
                    + "Sortable fields: name, email, role, active, createdAt.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of users")
    })
    public PageResponse<UserResponse> list(
            @CurrentCompany UUID companyId,
            @Parameter(description = "Part of the name, case-insensitive", example = "marta")
            @RequestParam(required = false) String name,
            @Parameter(description = "Only users with this role", example = "MANAGER")
            @RequestParam(required = false) Role role,
            @Parameter(description = "Only active (true) or inactive (false) users", example = "true")
            @RequestParam(required = false) Boolean active,
            @ParameterObject @PageableDefault(sort = "name") Pageable pageable) {
        return userService.findAll(companyId, name, role, active, pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a user", description = "Returns one user of the company.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The user"),
            @ApiResponse(responseCode = "404", description = "User not found in this company",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public UserResponse get(
            @CurrentCompany UUID companyId,
            @Parameter(description = "User identifier", example = "0d6c8e2a-4b1f-4c93-a7e5-3f9b1d5c7a20")
            @PathVariable UUID id) {
        return userService.findGetById(companyId, id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Update a user",
            description = "Changes the name and the role of a user. The e-mail cannot be changed. "
                    + "The company must keep at least one active administrator.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "404", description = "User not found in this company",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Would leave the company without an active administrator",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public UserResponse update(
            @CurrentCompany UUID companyId,
            @Parameter(description = "User identifier", example = "0d6c8e2a-4b1f-4c93-a7e5-3f9b1d5c7a20")
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request) {
        return userService.update(companyId, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Deactivate a user",
            description = "The user can no longer sign in and every open session is revoked. Data is kept. "
                    + "Idempotent. An access token already issued stays valid until it expires.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deactivated"),
            @ApiResponse(responseCode = "404", description = "User not found in this company",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Would leave the company without an active administrator",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public void deactivate(
            @CurrentCompany UUID companyId,
            @Parameter(description = "User identifier", example = "0d6c8e2a-4b1f-4c93-a7e5-3f9b1d5c7a20")
            @PathVariable UUID id) {
        userService.deactivate(companyId, id);
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Reactivate a user", description = "The user can sign in again. Idempotent.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User reactivated"),
            @ApiResponse(responseCode = "404", description = "User not found in this company",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public void activate(
            @CurrentCompany UUID companyId,
            @Parameter(description = "User identifier", example = "0d6c8e2a-4b1f-4c93-a7e5-3f9b1d5c7a20")
            @PathVariable UUID id) {
        userService.activate(companyId, id);
    }

    @PutMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Reset the password of a user",
            description = "Sets a new password and revokes every open session of the user, "
                    + "who must sign in again with the new password.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Password reset"),
            @ApiResponse(responseCode = "404", description = "User not found in this company",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public void resetPassword(
            @CurrentCompany UUID companyId,
            @Parameter(description = "User identifier", example = "0d6c8e2a-4b1f-4c93-a7e5-3f9b1d5c7a20")
            @PathVariable UUID id,
            @Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(companyId, id, request);
    }
}