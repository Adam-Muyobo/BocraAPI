/*
 * Persists and queries BOCRA person profiles by UUID and linked user UUID.
 */
package bw.org.bocra.api.person.repository;

import bw.org.bocra.api.person.entity.Person;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, UUID> {

    Optional<Person> findByUserUuid(UUID userUuid);
}
