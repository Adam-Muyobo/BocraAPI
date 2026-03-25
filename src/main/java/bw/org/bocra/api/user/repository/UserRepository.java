/*
 * Persists and queries BOCRA users by UUID, username, and email identifiers.
 */
package bw.org.bocra.api.user.repository;

import bw.org.bocra.api.user.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCase(String username);

    @Query("""
            select u
            from User u
            where lower(u.email) = lower(?1)
               or lower(u.username) = lower(?1)
            """)
    Optional<User> findByEmailOrUsername(String identifier);

    @EntityGraph(attributePaths = {"person", "organization"})
    Optional<User> findWithProfileByUuid(UUID uuid);

    @EntityGraph(attributePaths = {"person", "organization"})
    Optional<User> findWithPersonByUuid(UUID uuid);

    @EntityGraph(attributePaths = {"person", "organization"})
    Optional<User> findWithPersonByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = {"person", "organization"})
    @Query("""
            select u
            from User u
            where lower(u.email) = lower(?1)
               or lower(u.username) = lower(?1)
            """)
    Optional<User> findWithProfileByIdentifier(String identifier);

    @EntityGraph(attributePaths = {"person", "organization"})
    List<User> findAllByOrderByCreatedAtDesc();
}
