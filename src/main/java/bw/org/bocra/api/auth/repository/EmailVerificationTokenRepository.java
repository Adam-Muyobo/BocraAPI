/*
 * Persists email verification tokens and supports resend and confirmation workflows.
 */
package bw.org.bocra.api.auth.repository;

import bw.org.bocra.api.auth.entity.EmailVerificationToken;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, UUID> {

    Optional<EmailVerificationToken> findByTokenHash(String tokenHash);

    List<EmailVerificationToken> findAllByUserUuidAndConsumedAtIsNull(UUID userUuid);
}
