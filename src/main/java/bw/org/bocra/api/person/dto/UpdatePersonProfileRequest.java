/*
 * Carries validated profile updates for the authenticated BOCRA person's record.
 */
package bw.org.bocra.api.person.dto;

import bw.org.bocra.api.person.enums.Gender;
import bw.org.bocra.api.person.enums.NationalIdType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record UpdatePersonProfileRequest(
        @NotBlank(message = "Forenames are required.")
        @Size(max = 120, message = "Forenames must not exceed 120 characters.")
        String forenames,

        @NotBlank(message = "Surname is required.")
        @Size(max = 120, message = "Surname must not exceed 120 characters.")
        String surname,

        @NotNull(message = "Date of birth is required.")
        @Past(message = "Date of birth must be in the past.")
        LocalDate dateOfBirth,

        @NotNull(message = "Gender is required.")
        Gender gender,

        @NotBlank(message = "Nationality is required.")
        @Size(max = 120, message = "Nationality must not exceed 120 characters.")
        String nationality,

        @NotNull(message = "National ID type is required.")
        NationalIdType nationalIdType,

        @NotBlank(message = "Identity number is required.")
        @Size(max = 120, message = "Identity number must not exceed 120 characters.")
        String identityNumber,

        @NotBlank(message = "Phone number is required.")
        @Pattern(regexp = "^[+0-9 -]{7,30}$", message = "Phone number format is invalid.")
        String phoneNumber,

        @Pattern(regexp = "^$|^[+0-9 -]{7,30}$", message = "Alternate phone number format is invalid.")
        String alternatePhoneNumber,

        @NotBlank(message = "Residential address line 1 is required.")
        @Size(max = 255, message = "Residential address line 1 must not exceed 255 characters.")
        String residentialAddressLine1,

        @Size(max = 255, message = "Residential address line 2 must not exceed 255 characters.")
        String residentialAddressLine2,

        @NotBlank(message = "City is required.")
        @Size(max = 120, message = "City must not exceed 120 characters.")
        String city,

        @NotBlank(message = "District is required.")
        @Size(max = 120, message = "District must not exceed 120 characters.")
        String district,

        @NotBlank(message = "Country is required.")
        @Size(max = 120, message = "Country must not exceed 120 characters.")
        String country,

        @Size(max = 30, message = "Postal code must not exceed 30 characters.")
        String postalCode,

        @Size(max = 120, message = "Occupation must not exceed 120 characters.")
        String occupation,

        @Size(max = 180, message = "Organization name must not exceed 180 characters.")
        String organizationName,

        @Size(max = 500, message = "Profile photo URL must not exceed 500 characters.")
        String profilePhotoUrl
) {
}
