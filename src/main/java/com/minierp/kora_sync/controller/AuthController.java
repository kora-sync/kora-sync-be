package com.minierp.kora_sync.controller;

import com.minierp.kora_sync.dto.AuthResponse;
import com.minierp.kora_sync.dto.LoginRequest;
import com.minierp.kora_sync.dto.RegisterRequest;
import com.minierp.kora_sync.dto.UserResponse;
import com.minierp.kora_sync.security.AuthService;
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
@Tag(name = "Authentication", description = "Register a company, sign in and see who is signed in")
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