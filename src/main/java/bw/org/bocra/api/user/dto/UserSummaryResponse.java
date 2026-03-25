/*
 * Returns BOCRA user account details safe for API consumers.
 */
package bw.org.bocra.api.user.dto;

import bw.org.bocra.api.user.enums.AccountStatus;
import bw.org.bocra.api.user.enums.Role;
import bw.org.bocra.api.user.enums.UserType;
import java.time.Instant;
import java.util.UUID;

public record UserSummaryResponse(
        UUID uuid,
        String username,
        String email,
        UserType userType,
        Role role,
        AccountStatus accountStatus,
        boolean profileCompleted,
        Instant emailVerifiedAt,
        boolean enabled,
        boolean accountNonLocked,
        boolean credentialsNonExpired,
        boolean accountNonExpired,
        Instant lastLoginAt,
        Instant createdAt,
        Instant updatedAt
) {
}
