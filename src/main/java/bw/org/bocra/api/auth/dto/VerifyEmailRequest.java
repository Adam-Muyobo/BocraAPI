/*
 * Carries a one-time email verification token to activate a user account.
 */
package bw.org.bocra.api.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyEmailRequest(@NotBlank(message = "Verification token is required.") String token) {
}
