/*
 * Returns BOCRA person profile details without exposing persistence entities directly.
 */
package bw.org.bocra.api.person.dto;

import bw.org.bocra.api.person.enums.Gender;
import bw.org.bocra.api.person.enums.NationalIdType;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record PersonProfileResponse(
        UUID uuid,
        UUID userUuid,
        String forenames,
        String surname,
        String middleNames,
        LocalDate dateOfBirth,
        Gender gender,
        String nationality,
        NationalIdType nationalIdType,
        String nationalIdNumber,
        String passportNumber,
        String phoneNumber,
        String alternatePhoneNumber,
        String residentialAddressLine1,
        String residentialAddressLine2,
        String city,
        String district,
        String country,
        String postalCode,
        String occupation,
        String organizationName,
        String profilePhotoUrl,
        Instant createdAt,
        Instant updatedAt
) {
}
