/*
 * Returns post-registration state when a user is ready to sign in and complete onboarding.
 */
package bw.org.bocra.api.auth.dto;

import bw.org.bocra.api.user.dto.UserProfileResponse;

public record RegisterResponse(
        String message,
        UserProfileResponse user
) {
}
