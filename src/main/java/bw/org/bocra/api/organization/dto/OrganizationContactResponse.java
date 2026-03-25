/*
 * Returns organization contact people in API-safe form for management screens and workflows.
 */
package bw.org.bocra.api.organization.dto;

import java.time.Instant;
import java.util.UUID;

public record OrganizationContactResponse(
        UUID uuid,
        String forenames,
        String surname,
        String email,
        String phoneNumber,
        String jobTitle,
        boolean primaryContact,
        UUID linkedUserUuid,
        Instant createdAt,
        Instant updatedAt
) {
}
