/*
 * Persists and queries BOCRA users by UUID and email identifiers.
 */
package bw.org.bocra.api.user.repository;

import bw.org.bocra.api.user.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCase(String username);

    @EntityGraph(attributePaths = "person")
    Optional<User> findWithPersonByUuid(UUID uuid);

    @EntityGraph(attributePaths = "person")
    Optional<User> findWithPersonByEmailIgnoreCase(String email);
}
