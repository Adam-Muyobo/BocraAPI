/*
 * Carries validated minimal registration data before first-login onboarding begins.
 */
package bw.org.bocra.api.auth.dto;

import bw.org.bocra.api.user.enums.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @Email(message = "Email must be valid.")
        @NotBlank(message = "Email is required.")
        String email,

        @NotBlank(message = "Username is required.")
        @Size(max = 100, message = "Username must not exceed 100 characters.")
        String username,

        @NotBlank(message = "Password is required.")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters.")
        String password,

        UserType userType,

        @Size(max = 180, message = "Organization name must not exceed 180 characters.")
        String organizationDisplayName
) {
}
