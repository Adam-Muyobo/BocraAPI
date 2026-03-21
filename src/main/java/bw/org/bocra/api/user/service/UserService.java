/*
 * Coordinates BOCRA user account retrieval and mapping for authenticated and admin flows.
 */
package bw.org.bocra.api.user.service;

import bw.org.bocra.api.exception.ResourceNotFoundException;
import bw.org.bocra.api.person.dto.PersonProfileResponse;
import bw.org.bocra.api.person.entity.Person;
import bw.org.bocra.api.security.SecurityUtils;
import bw.org.bocra.api.user.dto.UserProfileResponse;
import bw.org.bocra.api.user.dto.UserSummaryResponse;
import bw.org.bocra.api.user.entity.User;
import bw.org.bocra.api.user.repository.UserRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile() {
        return toUserProfileResponse(getUserWithPerson(SecurityUtils.currentUserUuid()));
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(UUID userUuid) {
        return toUserProfileResponse(getUserWithPerson(userUuid));
    }

    @Transactional(readOnly = true)
    public User getCurrentUserEntity() {
        return getUserWithPerson(SecurityUtils.currentUserUuid());
    }

    @Transactional
    public void updateLastLogin(UUID userUuid) {
        User user = userRepository.findById(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        user.setLastLoginAt(java.time.Instant.now());
    }

    private User getUserWithPerson(UUID userUuid) {
        return userRepository.findWithPersonByUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    public UserSummaryResponse toUserSummaryResponse(User user) {
        return new UserSummaryResponse(
                user.getUuid(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getAccountStatus(),
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
        return new UserProfileResponse(toUserSummaryResponse(user), toPersonProfileResponse(user.getPerson()));
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
                person.getMiddleNames(),
                person.getDateOfBirth(),
                person.getGender(),
                person.getNationality(),
                person.getNationalIdType(),
                person.getNationalIdNumber(),
                person.getPassportNumber(),
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
}
