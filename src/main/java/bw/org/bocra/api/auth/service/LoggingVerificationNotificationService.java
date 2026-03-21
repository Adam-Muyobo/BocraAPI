/*
 * Provides a safe default notification adapter by logging verification tokens only when enabled.
 */
package bw.org.bocra.api.auth.service;

import bw.org.bocra.api.config.ApplicationProperties;
import bw.org.bocra.api.user.entity.User;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoggingVerificationNotificationService implements VerificationNotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingVerificationNotificationService.class);

    private final ApplicationProperties applicationProperties;

    public LoggingVerificationNotificationService(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Override
    public void dispatchVerificationToken(User user, String rawToken, Instant expiresAt) {
        if (applicationProperties.security().emailVerification().logGeneratedToken()) {
            LOGGER.info("Verification token for {} expires at {}: {}", user.getEmail(), expiresAt, rawToken);
            return;
        }

        LOGGER.info("Verification token generated for {} and expires at {}. Connect an email provider to deliver it.", user.getEmail(), expiresAt);
    }
}
