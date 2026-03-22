/*
 * Returns a BOCRA user account together with the linked person profile.
 */
package bw.org.bocra.api.user.dto;

import bw.org.bocra.api.organization.dto.OrganizationProfileResponse;
import bw.org.bocra.api.person.dto.PersonProfileResponse;

public record UserProfileResponse(
        UserSummaryResponse user,
        PersonProfileResponse person,
        OrganizationProfileResponse organization
) {
}
