/*
 * Persists and queries contacts that represent people acting on behalf of organizations.
 */
package bw.org.bocra.api.organization.repository;

import bw.org.bocra.api.organization.entity.OrganizationContactPerson;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationContactPersonRepository extends JpaRepository<OrganizationContactPerson, UUID> {

    List<OrganizationContactPerson> findAllByOrganizationUuidOrderByPrimaryContactDescCreatedAtAsc(UUID organizationUuid);

    Optional<OrganizationContactPerson> findByUuidAndOrganizationOwnerUserUuid(UUID contactUuid, UUID ownerUserUuid);
}
