/*
 * Enables Spring Data auditing for entity timestamp management.
 */
package bw.org.bocra.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
