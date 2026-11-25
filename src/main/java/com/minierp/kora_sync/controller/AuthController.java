package com.minierp.kora_sync.controller;

import com.minierp.kora_sync.dto.*;
import com.minierp.kora_sync.service.AuthService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register a company, sign in, renew the session, sign out and see who is signed in")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Register a company",
            description = "Creates a new company and its first user, who becomes the ADMIN, in a single step, "
                    + "and returns an access token so the user is already signed in. Public endpoint. "
                    + "The e-mail is unique in the whole system and the company tax id must not be registered yet.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Company and administrator created"),
            @ApiResponse(responseCode = "409", description = "E-mail or company tax id already registered",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Sign in",
            description = "Checks the e-mail and password and returns an access token. Public endpoint. "
                    + "The same generic error is returned whether the e-mail does not exist, the password is "
                    + "wrong or the user or company is inactive.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Signed in"),
            @ApiResponse(responseCode = "401", description = "Invalid e-mail or password",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public AuthResponse signin(@Valid @RequestBody LoginRequest request) {
        return authService.signin(request);
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Renew the access token",
            description = "Exchanges a valid refresh token for a new access token and a new refresh token. "
                    + "Public endpoint. The refresh token is single use: the one sent is invalidated. "
                    + "Sending a token that was already used revokes the whole session as a precaution.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "New access and refresh tokens"),
            @ApiResponse(responseCode = "401", description = "Refresh token invalid, expired, revoked or already used",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public AuthResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refresh(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Sign out",
            description = "Revokes the session of the given refresh token, so it can no longer be renewed. "
                    + "Public endpoint and idempotent: it answers 204 even if the token is unknown. "
                    + "An access token already issued stays valid until it expires.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Signed out")
    })
    public void logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request);
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Get the signed-in user",
            description = "Returns the user identified by the access token sent in the Authorization header.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Signed-in user"),
            @ApiResponse(responseCode = "404", description = "The user of the token no longer exists",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public UserResponse me() {
        return authService.me();
    }
}