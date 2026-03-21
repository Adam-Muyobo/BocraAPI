/*
 * Returns account verification status after email confirmation or resend operations.
 */
package bw.org.bocra.api.auth.dto;

import java.time.Instant;
import java.util.UUID;

public record VerificationStatusResponse(
        UUID userUuid,
        String email,
        boolean verified,
        Instant emailVerifiedAt,
        Instant verificationTokenExpiresAt
) {
}
