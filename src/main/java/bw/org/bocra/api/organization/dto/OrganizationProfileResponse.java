/*
 * Returns organization profile details without exposing persistence entities directly.
 */
package bw.org.bocra.api.organization.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrganizationProfileResponse(
        UUID uuid,
        UUID ownerUserUuid,
        String displayName,
        String tradingName,
        String registrationNumber,
        String taxIdentifier,
        String contactEmail,
        String contactPhoneNumber,
        String addressLine1,
        String addressLine2,
        String city,
        String district,
        String country,
        String postalCode,
        String logoUrl,
        List<OrganizationContactResponse> contacts,
        Instant createdAt,
        Instant updatedAt
) {
}
