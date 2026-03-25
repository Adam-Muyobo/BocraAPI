/*
 * Carries validated data for adding or updating organization contact people.
 */
package bw.org.bocra.api.organization.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

public record UpsertOrganizationContactRequest(
        @NotBlank(message = "Forenames are required.")
        @Size(max = 120, message = "Forenames must not exceed 120 characters.")
        String forenames,

        @NotBlank(message = "Surname is required.")
        @Size(max = 120, message = "Surname must not exceed 120 characters.")
        String surname,

        @Size(max = 150, message = "Email must not exceed 150 characters.")
        String email,

        @Pattern(regexp = "^$|^[+0-9 -]{7,30}$", message = "Phone number format is invalid.")
        String phoneNumber,

        @Size(max = 120, message = "Job title must not exceed 120 characters.")
        String jobTitle,

        boolean primaryContact
) {
}
