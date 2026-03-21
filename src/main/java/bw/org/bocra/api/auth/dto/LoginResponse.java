/*
 * Returns issued access and refresh tokens together with the authenticated user snapshot.
 */
package bw.org.bocra.api.auth.dto;

import bw.org.bocra.api.user.dto.UserProfileResponse;
import java.time.Instant;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Instant accessTokenExpiresAt,
        Instant refreshTokenExpiresAt,
        UserProfileResponse user
) {
}
