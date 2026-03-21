/*
 * Defines how generated email verification tokens are delivered to end users.
 */
package bw.org.bocra.api.auth.service;

import bw.org.bocra.api.user.entity.User;
import java.time.Instant;

public interface VerificationNotificationService {

    void dispatchVerificationToken(User user, String rawToken, Instant expiresAt);
}
