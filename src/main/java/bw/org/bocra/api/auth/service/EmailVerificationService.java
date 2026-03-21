/*
 * Manages email verification token lifecycle and account activation rules.
 */
package bw.org.bocra.api.auth.service;

import bw.org.bocra.api.auth.dto.ResendVerificationRequest;
import bw.org.bocra.api.auth.dto.VerificationStatusResponse;
import bw.org.bocra.api.auth.dto.VerifyEmailRequest;
import bw.org.bocra.api.auth.entity.EmailVerificationToken;
import bw.org.bocra.api.auth.repository.EmailVerificationTokenRepository;
import bw.org.bocra.api.common.util.SecureTokenUtils;
import bw.org.bocra.api.config.ApplicationProperties;
import bw.org.bocra.api.exception.BadRequestException;
import bw.org.bocra.api.exception.ResourceNotFoundException;
import bw.org.bocra.api.user.entity.User;
import bw.org.bocra.api.user.enums.AccountStatus;
import bw.org.bocra.api.user.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailVerificationService {

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final UserRepository userRepository;
    private final ApplicationProperties applicationProperties;
    private final VerificationNotificationService verificationNotificationService;

    public EmailVerificationService(
            EmailVerificationTokenRepository emailVerificationTokenRepository,
            UserRepository userRepository,
            ApplicationProperties applicationProperties,
            VerificationNotificationService verificationNotificationService
    ) {
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.userRepository = userRepository;
        this.applicationProperties = applicationProperties;
        this.verificationNotificationService = verificationNotificationService;
    }

    @Transactional
    public Instant createAndDispatch(User user) {
        List<EmailVerificationToken> activeTokens = emailVerificationTokenRepository.findAllByUserUuidAndConsumedAtIsNull(user.getUuid());
        Instant now = Instant.now();
        activeTokens.forEach(token -> token.setConsumedAt(now));

        String rawToken = SecureTokenUtils.generateOpaqueToken();
        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setTokenHash(SecureTokenUtils.sha256(rawToken));
        verificationToken.setExpiresAt(now.plusMillis(applicationProperties.security().emailVerification().expirationMs()));
        verificationToken.setUser(user);

        EmailVerificationToken savedToken = emailVerificationTokenRepository.save(verificationToken);
        verificationNotificationService.dispatchVerificationToken(user, rawToken, savedToken.getExpiresAt());
        return savedToken.getExpiresAt();
    }

    @Transactional
    public VerificationStatusResponse verify(VerifyEmailRequest request) {
        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByTokenHash(SecureTokenUtils.sha256(request.token()))
                .orElseThrow(() -> new BadRequestException("Verification token is invalid."));

        if (!verificationToken.isUsable()) {
            throw new BadRequestException("Verification token is expired or already used.");
        }

        User user = verificationToken.getUser();
        Instant now = Instant.now();
        verificationToken.setConsumedAt(now);
        user.setEmailVerifiedAt(now);
        user.setEnabled(true);
        user.setAccountStatus(AccountStatus.ACTIVE);

        return new VerificationStatusResponse(user.getUuid(), user.getEmail(), true, user.getEmailVerifiedAt(), null);
    }

    @Transactional
    public VerificationStatusResponse resend(ResendVerificationRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User account not found."));

        if (user.getEmailVerifiedAt() != null) {
            throw new BadRequestException("Email address has already been verified.");
        }

        Instant expiresAt = createAndDispatch(user);
        return new VerificationStatusResponse(user.getUuid(), user.getEmail(), false, null, expiresAt);
    }
}
