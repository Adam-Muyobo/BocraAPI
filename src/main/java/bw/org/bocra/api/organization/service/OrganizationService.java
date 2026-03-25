/*
 * Manages organization profiles and contact people for organization-owned accounts.
 */
package bw.org.bocra.api.organization.service;

import bw.org.bocra.api.exception.BadRequestException;
import bw.org.bocra.api.exception.ResourceNotFoundException;
import bw.org.bocra.api.organization.dto.OrganizationContactResponse;
import bw.org.bocra.api.organization.dto.OrganizationProfileResponse;
import bw.org.bocra.api.organization.dto.UpdateOrganizationProfileRequest;
import bw.org.bocra.api.organization.dto.UpsertOrganizationContactRequest;
import bw.org.bocra.api.organization.entity.Organization;
import bw.org.bocra.api.organization.entity.OrganizationContactPerson;
import bw.org.bocra.api.organization.repository.OrganizationContactPersonRepository;
import bw.org.bocra.api.organization.repository.OrganizationRepository;
import bw.org.bocra.api.security.SecurityUtils;
import bw.org.bocra.api.user.entity.User;
import bw.org.bocra.api.user.enums.UserType;
import bw.org.bocra.api.user.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/*
 * Organization onboarding and management remain separate from person affiliation data so
 * organizations can maintain their own structure and multiple contacts cleanly.
 */
@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationContactPersonRepository contactRepository;
    private final UserRepository userRepository;

    public OrganizationService(
            OrganizationRepository organizationRepository,
            OrganizationContactPersonRepository contactRepository,
            UserRepository userRepository
    ) {
        this.organizationRepository = organizationRepository;
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public OrganizationProfileResponse getCurrentOrganizationProfile() {
        return toProfileResponse(requireCurrentOrganization());
    }

    @Transactional
    public OrganizationProfileResponse updateCurrentOrganizationProfile(UpdateOrganizationProfileRequest request) {
        Organization organization = requireCurrentOrganization();
        organization.setDisplayName(request.displayName().trim());
        organization.setTradingName(trimToNull(request.tradingName()));
        organization.setRegistrationNumber(trimToNull(request.registrationNumber()));
        organization.setTaxIdentifier(trimToNull(request.taxIdentifier()));
        organization.setContactEmail(trimToNull(request.contactEmail()));
        organization.setContactPhoneNumber(trimToNull(request.contactPhoneNumber()));
        organization.setAddressLine1(trimToNull(request.addressLine1()));
        organization.setAddressLine2(trimToNull(request.addressLine2()));
        organization.setCity(trimToNull(request.city()));
        organization.setDistrict(trimToNull(request.district()));
        organization.setCountry(trimToNull(request.country()));
        organization.setPostalCode(trimToNull(request.postalCode()));
        organization.setLogoUrl(trimToNull(request.logoUrl()));

        organization.getOwnerUser().setProfileCompleted(true);
        return toProfileResponse(organization);
    }

    @Transactional(readOnly = true)
    public List<OrganizationContactResponse> listCurrentOrganizationContacts() {
        Organization organization = requireCurrentOrganization();
        return contactRepository.findAllByOrganizationUuidOrderByPrimaryContactDescCreatedAtAsc(organization.getUuid())
                .stream()
                .map(this::toContactResponse)
                .toList();
    }

    @Transactional
    public OrganizationContactResponse addCurrentOrganizationContact(UpsertOrganizationContactRequest request) {
        Organization organization = requireCurrentOrganization();
        OrganizationContactPerson contact = new OrganizationContactPerson();
        applyContact(contact, request, organization);
        return toContactResponse(contactRepository.save(contact));
    }

    @Transactional
    public OrganizationContactResponse updateCurrentOrganizationContact(UUID contactUuid, UpsertOrganizationContactRequest request) {
        OrganizationContactPerson contact = contactRepository.findByUuidAndOrganizationOwnerUserUuid(contactUuid, SecurityUtils.currentUserUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Organization contact not found."));
        applyContact(contact, request, contact.getOrganization());
        return toContactResponse(contact);
    }

    @Transactional(readOnly = true)
    public OrganizationProfileResponse toProfileResponse(Organization organization) {
        List<OrganizationContactResponse> contacts = contactRepository.findAllByOrganizationUuidOrderByPrimaryContactDescCreatedAtAsc(organization.getUuid())
                .stream()
                .map(this::toContactResponse)
                .toList();

        return new OrganizationProfileResponse(
                organization.getUuid(),
                organization.getOwnerUser().getUuid(),
                organization.getDisplayName(),
                organization.getTradingName(),
                organization.getRegistrationNumber(),
                organization.getTaxIdentifier(),
                organization.getContactEmail(),
                organization.getContactPhoneNumber(),
                organization.getAddressLine1(),
                organization.getAddressLine2(),
                organization.getCity(),
                organization.getDistrict(),
                organization.getCountry(),
                organization.getPostalCode(),
                organization.getLogoUrl(),
                contacts,
                organization.getCreatedAt(),
                organization.getUpdatedAt()
        );
    }

    public OrganizationContactResponse toContactResponse(OrganizationContactPerson contact) {
        return new OrganizationContactResponse(
                contact.getUuid(),
                contact.getForenames(),
                contact.getSurname(),
                contact.getEmail(),
                contact.getPhoneNumber(),
                contact.getJobTitle(),
                contact.isPrimaryContact(),
                contact.getLinkedUser() == null ? null : contact.getLinkedUser().getUuid(),
                contact.getCreatedAt(),
                contact.getUpdatedAt()
        );
    }

    @Transactional
    public Organization createMinimalOrganization(User ownerUser, String displayName) {
        Organization organization = new Organization();
        organization.setDisplayName(displayName.trim());
        ownerUser.attachOrganization(organization);
        return organization;
    }

    @Transactional
    public void linkContactsToUserIfPossible(User user) {
        if (!StringUtils.hasText(user.getEmail())) {
            return;
        }

        contactRepository.findAll().stream()
                .filter(contact -> user.getEmail().equalsIgnoreCase(contact.getEmail() == null ? "" : contact.getEmail()))
                .filter(contact -> contact.getLinkedUser() == null)
                .forEach(contact -> contact.setLinkedUser(user));
    }

    private Organization requireCurrentOrganization() {
        User currentUser = userRepository.findWithProfileByUuid(SecurityUtils.currentUserUuid())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (currentUser.getUserType() != UserType.ORGANIZATION) {
            throw new BadRequestException("Current user is not an organization account.");
        }

        return organizationRepository.findByOwnerUserUuid(currentUser.getUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Organization profile not found."));
    }

    private void applyContact(OrganizationContactPerson contact, UpsertOrganizationContactRequest request, Organization organization) {
        if (request.primaryContact()) {
            contactRepository.findAllByOrganizationUuidOrderByPrimaryContactDescCreatedAtAsc(organization.getUuid())
                    .forEach(existing -> existing.setPrimaryContact(false));
        }

        contact.setOrganization(organization);
        contact.setForenames(request.forenames().trim());
        contact.setSurname(request.surname().trim());
        contact.setEmail(trimToNull(request.email()));
        contact.setPhoneNumber(trimToNull(request.phoneNumber()));
        contact.setJobTitle(trimToNull(request.jobTitle()));
        contact.setPrimaryContact(request.primaryContact());
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
