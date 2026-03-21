/*
 * Manages refresh token issuance, rotation, lookup, and revocation.
 */
package bw.org.bocra.api.auth.service;

import bw.org.bocra.api.auth.entity.RefreshToken;
import bw.org.bocra.api.auth.repository.RefreshTokenRepository;
import bw.org.bocra.api.common.util.SecureTokenUtils;
import bw.org.bocra.api.exception.BadRequestException;
import bw.org.bocra.api.user.entity.User;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public IssuedRefreshToken issue(User user, long expirationMs) {
        String rawToken = SecureTokenUtils.generateOpaqueToken();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setTokenHash(SecureTokenUtils.sha256(rawToken));
        refreshToken.setExpiresAt(Instant.now().plusMillis(expirationMs));
        refreshToken.setUser(user);

        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        return new IssuedRefreshToken(rawToken, savedToken.getExpiresAt(), savedToken.getUuid());
    }

    @Transactional(readOnly = true)
    public RefreshToken requireActive(String rawToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(SecureTokenUtils.sha256(rawToken))
                .orElseThrow(() -> new BadRequestException("Refresh token is invalid."));

        if (!refreshToken.isActive()) {
            throw new BadRequestException("Refresh token is expired or revoked.");
        }

        return refreshToken;
    }

    @Transactional
    public void revoke(RefreshToken refreshToken) {
        refreshToken.setRevokedAt(Instant.now());
    }

    @Transactional
    public void revokeAndReplace(RefreshToken refreshToken, IssuedRefreshToken replacement) {
        refreshToken.setRevokedAt(Instant.now());
        refreshToken.setReplacedByTokenUuid(replacement.refreshTokenUuid());
    }

    @Transactional
    public void revokeAllActiveForUser(User user) {
        List<RefreshToken> activeTokens = refreshTokenRepository.findAllByUserUuidAndRevokedAtIsNull(user.getUuid());
        Instant revokedAt = Instant.now();
        activeTokens.forEach(token -> token.setRevokedAt(revokedAt));
    }

    public record IssuedRefreshToken(String rawToken, Instant expiresAt, java.util.UUID refreshTokenUuid) {
    }
}
