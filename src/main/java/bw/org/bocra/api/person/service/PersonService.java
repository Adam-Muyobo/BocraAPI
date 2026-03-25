/*
 * Manages profile retrieval and updates for the currently authenticated BOCRA person.
 */
package bw.org.bocra.api.person.service;

import bw.org.bocra.api.exception.BadRequestException;
import bw.org.bocra.api.exception.ResourceNotFoundException;
import bw.org.bocra.api.person.dto.PersonProfileResponse;
import bw.org.bocra.api.person.dto.UpdatePersonProfileRequest;
import bw.org.bocra.api.person.entity.Person;
import bw.org.bocra.api.person.repository.PersonRepository;
import bw.org.bocra.api.security.SecurityUtils;
import bw.org.bocra.api.user.entity.User;
import bw.org.bocra.api.user.enums.UserType;
import bw.org.bocra.api.user.repository.UserRepository;
import bw.org.bocra.api.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public PersonService(PersonRepository personRepository, UserRepository userRepository, UserService userService) {
        this.personRepository = personRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public PersonProfileResponse getCurrentProfile() {
        Person person = personRepository.findByUserUuid(SecurityUtils.currentUserUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Person profile not found."));
        return userService.toPersonProfileResponse(person);
    }

    @Transactional
    public PersonProfileResponse updateCurrentProfile(UpdatePersonProfileRequest request) {
        User user = userRepository.findWithProfileByUuid(SecurityUtils.currentUserUuid())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (user.getUserType() == UserType.ORGANIZATION) {
            throw new BadRequestException("Organization accounts should manage contacts separately from personal profiles.");
        }

        Person person = personRepository.findByUserUuid(user.getUuid()).orElseGet(() -> createPerson(user));

        person.setForenames(request.forenames().trim());
        person.setSurname(request.surname().trim());
        person.setDateOfBirth(request.dateOfBirth());
        person.setGender(request.gender());
        person.setNationality(request.nationality().trim());
        person.setNationalIdType(request.nationalIdType());
        person.setIdentityNumber(request.identityNumber().trim());
        person.setPhoneNumber(request.phoneNumber().trim());
        person.setAlternatePhoneNumber(trimToNull(request.alternatePhoneNumber()));
        person.setResidentialAddressLine1(request.residentialAddressLine1().trim());
        person.setResidentialAddressLine2(trimToNull(request.residentialAddressLine2()));
        person.setCity(request.city().trim());
        person.setDistrict(request.district().trim());
        person.setCountry(request.country().trim());
        person.setPostalCode(trimToNull(request.postalCode()));
        person.setOccupation(trimToNull(request.occupation()));
        person.setOrganizationName(trimToNull(request.organizationName()));
        person.setProfilePhotoUrl(trimToNull(request.profilePhotoUrl()));
        user.setProfileCompleted(true);

        return userService.toPersonProfileResponse(person);
    }

    private Person createPerson(User user) {
        Person person = new Person();
        user.attachPerson(person);
        return person;
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
