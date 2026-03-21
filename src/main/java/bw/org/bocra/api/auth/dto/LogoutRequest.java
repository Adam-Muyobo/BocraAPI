/*
 * Carries a refresh token that should be revoked during logout.
 */
package bw.org.bocra.api.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(@NotBlank(message = "Refresh token is required.") String refreshToken) {
}
