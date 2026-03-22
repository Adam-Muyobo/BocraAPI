/*
 * Seeds predictable local demo accounts so hackathon teammates can exercise key workflows quickly.
 */
package bw.org.bocra.api.config;

import bw.org.bocra.api.organization.entity.Organization;
import bw.org.bocra.api.organization.entity.OrganizationContactPerson;
import bw.org.bocra.api.organization.repository.OrganizationContactPersonRepository;
import bw.org.bocra.api.organization.service.OrganizationService;
import bw.org.bocra.api.person.entity.Person;
import bw.org.bocra.api.person.enums.Gender;
import bw.org.bocra.api.person.enums.NationalIdType;
import bw.org.bocra.api.user.entity.User;
import bw.org.bocra.api.user.enums.AccountStatus;
import bw.org.bocra.api.user.enums.Role;
import bw.org.bocra.api.user.enums.UserType;
import bw.org.bocra.api.user.repository.UserRepository;
import java.time.Instant;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/*
 * Demo data is intentionally lightweight and only created when explicitly enabled through
 * application properties so production environments stay clean.
 */
@Component
public class DemoDataSeeder implements CommandLineRunner {

    private final ApplicationProperties applicationProperties;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final OrganizationContactPersonRepository contactRepository;
    private final OrganizationService organizationService;

    public DemoDataSeeder(
            ApplicationProperties applicationProperties,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            OrganizationContactPersonRepository contactRepository,
            OrganizationService organizationService
    ) {
        this.applicationProperties = applicationProperties;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.contactRepository = contactRepository;
        this.organizationService = organizationService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!applicationProperties.seed().demoDataEnabled()) {
            return;
        }

        seedAdmin();
        seedIndividual();
        seedOrganization();
    }

    private void seedAdmin() {
        if (userRepository.existsByUsernameIgnoreCase("bocra.admin")) {
            return;
        }

        User admin = baseUser("bocra.admin", "admin@bocra.local", "Admin#12345", UserType.ADMIN, Role.ADMIN, true);
        Person person = new Person();
        person.setForenames("System Admin");
        person.setSurname("BOCRA");
        person.setDateOfBirth(LocalDate.of(1990, 1, 1));
        person.setGender(Gender.PREFER_NOT_TO_SAY);
        person.setNationality("Botswana");
        person.setNationalIdType(NationalIdType.OMANG);
        person.setIdentityNumber("ADMIN-0001");
        person.setPhoneNumber("+267 71 000 001");
        person.setResidentialAddressLine1("BOCRA Headquarters");
        person.setCity("Gaborone");
        person.setDistrict("South East");
        person.setCountry("Botswana");
        admin.attachPerson(person);
        userRepository.save(admin);
    }

    private void seedIndividual() {
        if (userRepository.existsByUsernameIgnoreCase("mothusi.demo")) {
            return;
        }

        User individual = baseUser("mothusi.demo", "mothusi.demo@bocra.local", "Password#123", UserType.INDIVIDUAL, Role.APPLICANT, true);
        Person person = new Person();
        person.setForenames("Mothusi");
        person.setSurname("Kgosi");
        person.setDateOfBirth(LocalDate.of(1995, 6, 12));
        person.setGender(Gender.MALE);
        person.setNationality("Botswana");
        person.setNationalIdType(NationalIdType.OMANG);
        person.setIdentityNumber("BN1234567");
        person.setPhoneNumber("+267 71 234 567");
        person.setResidentialAddressLine1("Plot 1234, Block 6");
        person.setCity("Gaborone");
        person.setDistrict("South East");
        person.setCountry("Botswana");
        person.setOccupation("Telecom Analyst");
        person.setOrganizationName("DemoTel Botswana");
        individual.attachPerson(person);
        userRepository.save(individual);
    }

    private void seedOrganization() {
        if (userRepository.existsByUsernameIgnoreCase("demotel.org")) {
            return;
        }

        User organizationUser = baseUser("demotel.org", "admin@demotel.local", "Password#123", UserType.ORGANIZATION, Role.APPLICANT, true);
        Organization organization = organizationService.createMinimalOrganization(organizationUser, "DemoTel Botswana");
        organization.setTradingName("DemoTel");
        organization.setRegistrationNumber("BW-REG-2026-001");
        organization.setContactEmail("info@demotel.local");
        organization.setContactPhoneNumber("+267 72 111 222");
        organization.setCountry("Botswana");
        organization.setCity("Gaborone");
        organization.setDistrict("South East");
        organization.setAddressLine1("Plot 889, CBD");
        organizationUser.attachOrganization(organization);

        User savedOrganizationUser = userRepository.save(organizationUser);

        OrganizationContactPerson contact = new OrganizationContactPerson();
        contact.setOrganization(savedOrganizationUser.getOrganization());
        contact.setForenames("Kagiso");
        contact.setSurname("Molefe");
        contact.setEmail("kagiso@demotel.local");
        contact.setPhoneNumber("+267 73 222 333");
        contact.setJobTitle("Regulatory Liaison");
        contact.setPrimaryContact(true);
        contactRepository.save(contact);
    }

    private User baseUser(String username, String email, String rawPassword, UserType userType, Role role, boolean profileCompleted) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setUserType(userType);
        user.setRole(role);
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setProfileCompleted(profileCompleted);
        user.setEmailVerifiedAt(Instant.now());
        return user;
    }
}
