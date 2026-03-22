/*
 * Stores organization contacts that may optionally later link to their own BOCRA user account.
 */
package bw.org.bocra.api.organization.entity;

import bw.org.bocra.api.common.entity.BaseEntity;
import bw.org.bocra.api.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * Contact records are intentionally separate from user accounts so an organization can add and
 * manage contacts before those people create their own credentials.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "organization_contact_people")
public class OrganizationContactPerson extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String forenames;

    @Column(nullable = false, length = 120)
    private String surname;

    @Column(length = 150)
    private String email;

    @Column(length = 30)
    private String phoneNumber;

    @Column(length = 120)
    private String jobTitle;

    @Column(nullable = false)
    private boolean primaryContact;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_uuid", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linked_user_uuid")
    private User linkedUser;
}
