/*
 * Returns BOCRA user account details safe for API consumers.
 */
package bw.org.bocra.api.user.dto;

import bw.org.bocra.api.user.enums.AccountStatus;
import bw.org.bocra.api.user.enums.Role;
import java.time.Instant;
import java.util.UUID;

public record UserSummaryResponse(
        UUID uuid,
        String username,
        String email,
        Role role,
        AccountStatus accountStatus,
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
