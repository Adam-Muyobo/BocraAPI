/*
 * Handles applicant registration, credential authentication, and current-user retrieval.
 */
package bw.org.bocra.api.auth.service;

import bw.org.bocra.api.auth.dto.CurrentUserResponse;
import bw.org.bocra.api.auth.dto.LoginRequest;
import bw.org.bocra.api.auth.dto.LoginResponse;
import bw.org.bocra.api.auth.dto.LogoutRequest;
import bw.org.bocra.api.auth.dto.RefreshTokenRequest;
import bw.org.bocra.api.auth.dto.RegisterRequest;
import bw.org.bocra.api.auth.dto.RegisterResponse;
import bw.org.bocra.api.exception.BadRequestException;
import bw.org.bocra.api.exception.ConflictException;
import bw.org.bocra.api.person.entity.Person;
import bw.org.bocra.api.person.enums.NationalIdType;
import bw.org.bocra.api.security.SecurityUser;
import bw.org.bocra.api.security.jwt.JwtService;
import bw.org.bocra.api.user.dto.UserProfileResponse;
import bw.org.bocra.api.user.entity.User;
import bw.org.bocra.api.user.enums.AccountStatus;
import bw.org.bocra.api.user.enums.Role;
import bw.org.bocra.api.user.repository.UserRepository;
import bw.org.bocra.api.user.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final EmailVerificationService emailVerificationService;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            UserService userService,
            RefreshTokenService refreshTokenService,
            EmailVerificationService emailVerificationService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.emailVerificationService = emailVerificationService;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        validateRegistrationRequest(request);

        if (userRepository.existsByEmailIgnoreCase(request.email().trim())) {
            throw new ConflictException("A user with this email already exists.");
        }

        if (StringUtils.hasText(request.username()) && userRepository.existsByUsernameIgnoreCase(request.username().trim())) {
            throw new ConflictException("A user with this username already exists.");
        }

        User user = new User();
        user.setEmail(request.email().trim().toLowerCase());
        user.setUsername(trimToNull(request.username()));
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(Role.APPLICANT);
        user.setAccountStatus(AccountStatus.PENDING_VERIFICATION);
        user.setEnabled(false);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);

        Person person = new Person();
        person.setForenames(request.forenames().trim());
        person.setSurname(request.surname().trim());
        person.setMiddleNames(trimToNull(request.middleNames()));
        person.setDateOfBirth(request.dateOfBirth());
        person.setGender(request.gender());
        person.setNationality(request.nationality().trim());
        person.setNationalIdType(request.nationalIdType());
        person.setNationalIdNumber(request.nationalIdNumber().trim());
        person.setPassportNumber(trimToNull(request.passportNumber()));
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

        user.attachPerson(person);
        User savedUser = userRepository.save(user);
        java.time.Instant verificationTokenExpiresAt = emailVerificationService.createAndDispatch(savedUser);

        return new RegisterResponse(
                "Registration completed. Verify your email address before logging in.",
                verificationTokenExpiresAt,
                true,
                userService.toUserProfileResponse(savedUser)
        );
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email().trim(), request.password()));

        User user = userRepository.findWithPersonByEmailIgnoreCase(request.email().trim())
                .orElseThrow(() -> new BadRequestException("User account not found."));

        user.setLastLoginAt(java.time.Instant.now());

        return buildLoginResponse(user);
    }

    @Transactional
    public LoginResponse refresh(RefreshTokenRequest request) {
        var existingToken = refreshTokenService.requireActive(request.refreshToken());
        User user = userRepository.findWithPersonByUuid(existingToken.getUser().getUuid())
                .orElseThrow(() -> new BadRequestException("User account not found."));

        TokenBundle tokenBundle = issueTokenBundle(user);
        refreshTokenService.revokeAndReplace(existingToken, tokenBundle.refreshToken());
        return tokenBundle.response();
    }

    @Transactional
    public void logout(LogoutRequest request) {
        var refreshToken = refreshTokenService.requireActive(request.refreshToken());
        refreshTokenService.revoke(refreshToken);
    }

    @Transactional(readOnly = true)
    public CurrentUserResponse currentUser() {
        return new CurrentUserResponse(userService.getCurrentUserProfile());
    }

    private void validateRegistrationRequest(RegisterRequest request) {
        if (request.nationalIdType() == NationalIdType.PASSPORT && !StringUtils.hasText(request.passportNumber())) {
            throw new BadRequestException("Passport number is required when national ID type is PASSPORT.");
        }
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private LoginResponse buildLoginResponse(User user) {
        return issueTokenBundle(user).response();
    }

    private TokenBundle issueTokenBundle(User user) {
        UserProfileResponse userProfileResponse = userService.toUserProfileResponse(user);
        String accessToken = jwtService.generateToken(SecurityUser.from(user));
        RefreshTokenService.IssuedRefreshToken refreshToken = refreshTokenService.issue(
                user,
                jwtService.refreshTokenExpirationMs()
        );

        return new TokenBundle(
                new LoginResponse(
                        accessToken,
                        refreshToken.rawToken(),
                        "Bearer",
                        jwtService.extractExpiration(accessToken),
                        refreshToken.expiresAt(),
                        userProfileResponse
                ),
                refreshToken
        );
    }

    private record TokenBundle(LoginResponse response, RefreshTokenService.IssuedRefreshToken refreshToken) {
    }
}
