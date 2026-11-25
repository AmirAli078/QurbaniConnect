package com.qurbaniconnect.controller;

import com.qurbaniconnect.dto.request.*;
import com.qurbaniconnect.dto.response.*;
import com.qurbaniconnect.security.CurrentUser;
import com.qurbaniconnect.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register/customer")
    @Operation(summary = "Register a customer", description = "Public. Creates a customer account and profile. 409 if phone/email exists.")
    public ResponseEntity<ApiResponse<UserResponse>> registerCustomer(@Valid @RequestBody RegisterCustomerRequest r) {
        log.info("Registering customer with email={}, phone={}", r.email(), r.phoneNumber());
        UserResponse response = authService.registerCustomer(r);
        log.info("Customer registered successfully with id={}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Customer registered successfully", response));
    }

    @PostMapping("/register/qasai")
    @Operation(summary = "Register a Qasai", description = "Public. Profile starts as PENDING until an admin approves it. Admin accounts cannot be registered publicly.")
    public ResponseEntity<ApiResponse<UserResponse>> registerQasai(@Valid @RequestBody RegisterQasaiRequest r) {
        log.info("Registering Qasai with email={}, phone={}", r.email(), r.phoneNumber());
        UserResponse response = authService.registerQasai(r);
        log.info("Qasai registered successfully with id={}, awaiting admin verification", response.id());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Qasai registered. Awaiting admin verification", response));
    }

    @PostMapping("/login")
    @Operation(summary = "Login with email or phone", description = "Returns an access JWT and a rotating refresh token.")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest r) {
        log.info("Login attempt for identifier={}", r.identifier());
        AuthResponse response = authService.login(r);
        log.info("Login successful for userId={}", response.user().id());
        return ApiResponse.ok("Login successful", response);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Exchange a refresh token for new tokens", description = "Refresh tokens are single-use (rotated).")
    public ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest r) {
        log.info("Token refresh request");
        AuthResponse response = authService.refresh(r.refreshToken());
        log.info("Token refreshed successfully for userId={}", response.user().id());
        return ApiResponse.ok("Token refreshed", response);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request a password reset", description = "Always returns 200 to avoid revealing which accounts exist.")
    public ApiResponse<Void> forgot(@Valid @RequestBody ForgotPasswordRequest r) {
        log.info("Password reset request for identifier={}", r.identifier());
        authService.forgotPassword(r.identifier());
        log.info("Password reset request processed for identifier={}", r.identifier());
        return ApiResponse.ok("If the account exists, reset instructions have been issued", null);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password with a token")
    public ApiResponse<Void> reset(@Valid @RequestBody ResetPasswordRequest r) {
        log.info("Password reset attempt with token");
        authService.resetPassword(r);
        log.info("Password reset completed successfully");
        return ApiResponse.ok("Password reset successful", null);
    }

    @PostMapping("/logout")
    @Operation(summary = "Revoke a refresh token", description = "Requires authentication.")
    public ApiResponse<Void> logout(@Valid @RequestBody RefreshTokenRequest r) {
        log.info("Logout request for userId={}", CurrentUser.id());
        authService.logout(CurrentUser.id(), r.refreshToken());
        log.info("User logged out successfully userId={}", CurrentUser.id());
        return ApiResponse.ok("Logged out", null);
    }

    @GetMapping("/me")
    @Operation(summary = "Current user", description = "Requires authentication.")
    public ApiResponse<UserResponse> me() {
        log.info("Fetching current user profile for userId={}", CurrentUser.id());
        UserResponse response = authService.me(CurrentUser.id());
        log.info("Current user profile fetched for userId={}", response.id());
        return ApiResponse.ok(response);
    }
}
