/*
 * Persists and queries organization profiles owned by BOCRA organization accounts.
 */
package bw.org.bocra.api.organization.repository;

import bw.org.bocra.api.organization.entity.Organization;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    @EntityGraph(attributePaths = "contacts")
    Optional<Organization> findByOwnerUserUuid(UUID ownerUserUuid);
}
