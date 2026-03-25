/*
 * Stores organization details for organization-owned BOCRA accounts.
 */
package bw.org.bocra.api.organization.entity;

import bw.org.bocra.api.common.entity.BaseEntity;
import bw.org.bocra.api.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * The organization record holds the public organization profile while remaining independent from
 * the free-text organization affiliation field that may appear on person profiles.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "organizations")
public class Organization extends BaseEntity {

    @Column(nullable = false, length = 180)
    private String displayName;

    @Column(length = 180)
    private String tradingName;

    @Column(length = 120)
    private String registrationNumber;

    @Column(length = 120)
    private String taxIdentifier;

    @Column(length = 150)
    private String contactEmail;

    @Column(length = 30)
    private String contactPhoneNumber;

    @Column(length = 255)
    private String addressLine1;

    @Column(length = 255)
    private String addressLine2;

    @Column(length = 120)
    private String city;

    @Column(length = 120)
    private String district;

    @Column(length = 120)
    private String country;

    @Column(length = 30)
    private String postalCode;

    @Column(length = 500)
    private String logoUrl;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_user_uuid", nullable = false, unique = true)
    private User ownerUser;

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
    private List<OrganizationContactPerson> contacts = new ArrayList<>();
}
