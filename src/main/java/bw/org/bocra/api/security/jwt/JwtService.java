/*
 * Issues and validates signed JWT access tokens for stateless API authentication.
 */
package bw.org.bocra.api.security.jwt;

import bw.org.bocra.api.config.ApplicationProperties;
import bw.org.bocra.api.security.SecurityUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final ApplicationProperties applicationProperties;

    public JwtService(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public String generateToken(SecurityUser securityUser) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(applicationProperties.security().jwt().expirationMs());

        return Jwts.builder()
                .subject(securityUser.getUsername())
                .claims(Map.of(
                        "userUuid", securityUser.userUuid().toString(),
                        "authorities", securityUser.getAuthorities().stream().map(authority -> authority.getAuthority()).toList()
                ))
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(signingKey())
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public UUID extractUserUuid(String token) {
        return UUID.fromString(parseClaims(token).get("userUuid", String.class));
    }

    public Instant extractExpiration(String token) {
        return parseClaims(token).getExpiration().toInstant();
    }

    public long refreshTokenExpirationMs() {
        return applicationProperties.security().refreshToken().expirationMs();
    }

    public boolean isTokenValid(String token, SecurityUser securityUser) {
        Claims claims = parseClaims(token);
        return claims.getSubject().equalsIgnoreCase(securityUser.getUsername())
                && claims.getExpiration().after(new Date())
                && UUID.fromString(claims.get("userUuid", String.class)).equals(securityUser.userUuid());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(resolveSecretBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Key signingKey() {
        return Keys.hmacShaKeyFor(resolveSecretBytes());
    }

    private byte[] resolveSecretBytes() {
        String secret = applicationProperties.security().jwt().secret();
        try {
            return Decoders.BASE64.decode(secret);
        } catch (IllegalArgumentException ignored) {
            return secret.getBytes();
        }
    }
}
