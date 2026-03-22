/*
 * Stores profile and identity details linked one-to-one with an authenticated user.
 */
package bw.org.bocra.api.person.entity;

import bw.org.bocra.api.common.entity.BaseEntity;
import bw.org.bocra.api.person.enums.Gender;
import bw.org.bocra.api.person.enums.NationalIdType;
import bw.org.bocra.api.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "persons")
public class Person extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String forenames;

    @Column(nullable = false, length = 120)
    private String surname;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Gender gender;

    @Column(nullable = false, length = 120)
    private String nationality;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private NationalIdType nationalIdType;

    @Column(nullable = false, length = 120)
    private String identityNumber;

    @Column(nullable = false, length = 30)
    private String phoneNumber;

    @Column(length = 30)
    private String alternatePhoneNumber;

    @Column(nullable = false, length = 255)
    private String residentialAddressLine1;

    @Column(length = 255)
    private String residentialAddressLine2;

    @Column(nullable = false, length = 120)
    private String city;

    @Column(nullable = false, length = 120)
    private String district;

    @Column(nullable = false, length = 120)
    private String country;

    @Column(length = 30)
    private String postalCode;

    @Column(length = 120)
    private String occupation;

    @Column(length = 180)
    private String organizationName;

    @Column(length = 500)
    private String profilePhotoUrl;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_uuid", nullable = false, unique = true)
    private User user;
}
