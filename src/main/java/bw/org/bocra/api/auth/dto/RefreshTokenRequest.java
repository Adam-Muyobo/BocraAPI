/*
 * Carries an opaque refresh token used to rotate access credentials securely.
 */
package bw.org.bocra.api.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(@NotBlank(message = "Refresh token is required.") String refreshToken) {
}
