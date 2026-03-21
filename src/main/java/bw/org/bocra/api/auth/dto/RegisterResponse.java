/*
 * Returns post-registration state when a user must verify their email before logging in.
 */
package bw.org.bocra.api.auth.dto;

import bw.org.bocra.api.user.dto.UserProfileResponse;
import java.time.Instant;

public record RegisterResponse(
        String message,
        Instant verificationTokenExpiresAt,
        boolean emailVerificationRequired,
        UserProfileResponse user
) {
}
