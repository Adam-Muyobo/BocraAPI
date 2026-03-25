/*
 * Coordinates BOCRA user account retrieval and mapping for authenticated and admin flows.
 */
package bw.org.bocra.api.user.service;

import bw.org.bocra.api.exception.ResourceNotFoundException;
import bw.org.bocra.api.organization.dto.OrganizationProfileResponse;
import bw.org.bocra.api.organization.entity.Organization;
import bw.org.bocra.api.organization.service.OrganizationService;
import bw.org.bocra.api.person.dto.PersonProfileResponse;
import bw.org.bocra.api.person.entity.Person;
import bw.org.bocra.api.security.SecurityUtils;
import bw.org.bocra.api.user.dto.UserProfileResponse;
import bw.org.bocra.api.user.dto.UserSummaryResponse;
import bw.org.bocra.api.user.entity.User;
import bw.org.bocra.api.user.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 * This service centralizes user-facing DTO mapping so controllers stay thin and other modules can
 * compose user, person, and organization data consistently.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final OrganizationService organizationService;

    public UserService(UserRepository userRepository, OrganizationService organizationService) {
        this.userRepository = userRepository;
        this.organizationService = organizationService;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile() {
        return toUserProfileResponse(getUserWithProfile(SecurityUtils.currentUserUuid()));
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(UUID userUuid) {
        return toUserProfileResponse(getUserWithProfile(userUuid));
    }

    @Transactional(readOnly = true)
    public List<UserSummaryResponse> listUsers() {
        return userRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toUserSummaryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public User getCurrentUserEntity() {
        return getUserWithProfile(SecurityUtils.currentUserUuid());
    }

    @Transactional(readOnly = true)
    public User getUserByIdentifier(String identifier) {
        return userRepository.findWithProfileByIdentifier(identifier)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    @Transactional
    public void updateLastLogin(UUID userUuid) {
        User user = userRepository.findById(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        user.setLastLoginAt(Instant.now());
    }

    private User getUserWithProfile(UUID userUuid) {
        return userRepository.findWithProfileByUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    public UserSummaryResponse toUserSummaryResponse(User user) {
        return new UserSummaryResponse(
                user.getUuid(),
                user.getUsername(),
                user.getEmail(),
                user.getUserType(),
                user.getRole(),
                user.getAccountStatus(),
                user.isProfileCompleted(),
                user.getEmailVerifiedAt(),
                user.isEnabled(),
                user.isAccountNonLocked(),
                user.isCredentialsNonExpired(),
                user.isAccountNonExpired(),
                user.getLastLoginAt(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public UserProfileResponse toUserProfileResponse(User user) {
        return new UserProfileResponse(
                toUserSummaryResponse(user),
                toPersonProfileResponse(user.getPerson()),
                toOrganizationProfileResponse(user.getOrganization())
        );
    }

    public PersonProfileResponse toPersonProfileResponse(Person person) {
        if (person == null) {
            return null;
        }

        return new PersonProfileResponse(
                person.getUuid(),
                person.getUser().getUuid(),
                person.getForenames(),
                person.getSurname(),
                person.getDateOfBirth(),
                person.getGender(),
                person.getNationality(),
                person.getNationalIdType(),
                person.getIdentityNumber(),
                person.getPhoneNumber(),
                person.getAlternatePhoneNumber(),
                person.getResidentialAddressLine1(),
                person.getResidentialAddressLine2(),
                person.getCity(),
                person.getDistrict(),
                person.getCountry(),
                person.getPostalCode(),
                person.getOccupation(),
                person.getOrganizationName(),
                person.getProfilePhotoUrl(),
                person.getCreatedAt(),
                person.getUpdatedAt()
        );
    }

    public OrganizationProfileResponse toOrganizationProfileResponse(Organization organization) {
        if (organization == null) {
            return null;
        }
        return organizationService.toProfileResponse(organization);
    }
}
