/*
 * Carries the email address that should receive a fresh verification token.
 */
package bw.org.bocra.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResendVerificationRequest(
        @NotBlank(message = "Email is required.")
        @Email(message = "Email must be valid.")
        String email
) {
}
