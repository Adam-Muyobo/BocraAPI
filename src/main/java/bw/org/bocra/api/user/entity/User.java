/*
 * Stores authentication, authorization, and account lifecycle data for a platform user.
 */
package bw.org.bocra.api.user.entity;

import bw.org.bocra.api.common.entity.BaseEntity;
import bw.org.bocra.api.person.entity.Person;
import bw.org.bocra.api.user.enums.AccountStatus;
import bw.org.bocra.api.user.enums.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(unique = true)
    private String username;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AccountStatus accountStatus;

    @Column(nullable = false)
    private boolean isEnabled;

    @Column(nullable = false)
    private boolean isAccountNonLocked;

    @Column(nullable = false)
    private boolean isCredentialsNonExpired;

    @Column(nullable = false)
    private boolean isAccountNonExpired;

    private Instant emailVerifiedAt;

    private Instant lastLoginAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Person person;

    public void attachPerson(Person person) {
        this.person = person;
        person.setUser(this);
    }
}
