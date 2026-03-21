/*
 * Binds application-specific settings such as JWT and CORS configuration.
 */
package bw.org.bocra.api.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application")
public record ApplicationProperties(Security security) {

    public record Security(Jwt jwt, RefreshToken refreshToken, EmailVerification emailVerification, Cors cors) {
    }

    public record Jwt(String secret, long expirationMs) {
    }

    public record RefreshToken(long expirationMs) {
    }

    public record EmailVerification(long expirationMs, boolean logGeneratedToken) {
    }

    public record Cors(List<String> allowedOrigins) {
    }
}
