/*
 * Exposes helper methods for retrieving the currently authenticated BOCRA user.
 */
package bw.org.bocra.api.security;

import bw.org.bocra.api.exception.UnauthorizedException;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static UUID currentUserUuid() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof SecurityUser securityUser)) {
            throw new UnauthorizedException("Authentication is required.");
        }
        return securityUser.userUuid();
    }
}
