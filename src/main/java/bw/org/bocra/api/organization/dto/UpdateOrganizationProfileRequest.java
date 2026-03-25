/*
 * Carries validated updates for an organization account's primary profile details.
 */
package bw.org.bocra.api.organization.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateOrganizationProfileRequest(
        @NotBlank(message = "Organization display name is required.")
        @Size(max = 180, message = "Organization display name must not exceed 180 characters.")
        String displayName,

        @Size(max = 180, message = "Trading name must not exceed 180 characters.")
        String tradingName,

        @Size(max = 120, message = "Registration number must not exceed 120 characters.")
        String registrationNumber,

        @Size(max = 120, message = "Tax identifier must not exceed 120 characters.")
        String taxIdentifier,

        @Size(max = 150, message = "Contact email must not exceed 150 characters.")
        String contactEmail,

        @Pattern(regexp = "^$|^[+0-9 -]{7,30}$", message = "Contact phone number format is invalid.")
        String contactPhoneNumber,

        @Size(max = 255, message = "Address line 1 must not exceed 255 characters.")
        String addressLine1,

        @Size(max = 255, message = "Address line 2 must not exceed 255 characters.")
        String addressLine2,

        @Size(max = 120, message = "City must not exceed 120 characters.")
        String city,

        @Size(max = 120, message = "District must not exceed 120 characters.")
        String district,

        @Size(max = 120, message = "Country must not exceed 120 characters.")
        String country,

        @Size(max = 30, message = "Postal code must not exceed 30 characters.")
        String postalCode,

        @Size(max = 500, message = "Logo URL must not exceed 500 characters.")
        String logoUrl
) {
}
