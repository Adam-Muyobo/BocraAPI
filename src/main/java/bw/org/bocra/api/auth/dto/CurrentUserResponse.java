/*
 * Returns the currently authenticated BOCRA user and linked person profile.
 */
package bw.org.bocra.api.auth.dto;

import bw.org.bocra.api.user.dto.UserProfileResponse;

public record CurrentUserResponse(UserProfileResponse user) {
}
