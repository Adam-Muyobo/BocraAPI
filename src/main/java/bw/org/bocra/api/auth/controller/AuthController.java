/*
 * Exposes public authentication endpoints and current-user retrieval for BOCRA clients.
 */
package bw.org.bocra.api.auth.controller;

import bw.org.bocra.api.auth.dto.CurrentUserResponse;
import bw.org.bocra.api.auth.dto.LoginRequest;
import bw.org.bocra.api.auth.dto.LoginResponse;
import bw.org.bocra.api.auth.dto.LogoutRequest;
import bw.org.bocra.api.auth.dto.RefreshTokenRequest;
import bw.org.bocra.api.auth.dto.RegisterRequest;
import bw.org.bocra.api.auth.dto.RegisterResponse;
import bw.org.bocra.api.auth.dto.ResendVerificationRequest;
import bw.org.bocra.api.auth.dto.VerificationStatusResponse;
import bw.org.bocra.api.auth.dto.VerifyEmailRequest;
import bw.org.bocra.api.auth.service.AuthService;
import bw.org.bocra.api.auth.service.EmailVerificationService;
import bw.org.bocra.api.common.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;

    public AuthController(AuthService authService, EmailVerificationService emailVerificationService) {
        this.authService = authService;
        this.emailVerificationService = emailVerificationService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration completed successfully.", authService.register(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Login completed successfully.", authService.login(request)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Token refresh completed successfully.", authService.refresh(request)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(ApiResponse.success("Logout completed successfully.", null));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<VerificationStatusResponse>> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Email verification completed successfully.", emailVerificationService.verify(request)));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<VerificationStatusResponse>> resendVerification(
            @Valid @RequestBody ResendVerificationRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Verification email has been regenerated successfully.",
                emailVerificationService.resend(request)
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CurrentUserResponse>> currentUser() {
        return ResponseEntity.ok(ApiResponse.success("Current user retrieved successfully.", authService.currentUser()));
    }
}
