/*
 * Persists refresh tokens and supports lookup and revocation workflows.
 */
package bw.org.bocra.api.auth.repository;

import bw.org.bocra.api.auth.entity.RefreshToken;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findAllByUserUuidAndRevokedAtIsNull(UUID userUuid);

    void deleteAllByExpiresAtBefore(Instant instant);
}
