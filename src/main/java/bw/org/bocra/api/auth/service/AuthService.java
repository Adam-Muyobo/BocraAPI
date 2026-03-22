/*
 * Handles registration, credential authentication, token refresh, and current-user retrieval.
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
import bw.org.bocra.api.organization.service.OrganizationService;
import bw.org.bocra.api.security.SecurityUser;
import bw.org.bocra.api.security.jwt.JwtService;
import bw.org.bocra.api.user.dto.UserProfileResponse;
import bw.org.bocra.api.user.entity.User;
import bw.org.bocra.api.user.enums.AccountStatus;
import bw.org.bocra.api.user.enums.Role;
import bw.org.bocra.api.user.enums.UserType;
import bw.org.bocra.api.user.repository.UserRepository;
import bw.org.bocra.api.user.service.UserService;
import java.time.Instant;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/*
 * Registration is intentionally minimal so first-login onboarding can collect richer profile data
 * without overwhelming new users at account creation time.
 */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final OrganizationService organizationService;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            UserService userService,
            RefreshTokenService refreshTokenService,
            OrganizationService organizationService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.organizationService = organizationService;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        validateRegistrationRequest(request);

        if (userRepository.existsByEmailIgnoreCase(request.email().trim())) {
            throw new ConflictException("A user with this email already exists.");
        }

        if (userRepository.existsByUsernameIgnoreCase(request.username().trim())) {
            throw new ConflictException("A user with this username already exists.");
        }

        User user = new User();
        user.setEmail(request.email().trim().toLowerCase());
        user.setUsername(request.username().trim());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setUserType(request.userType());
        user.setRole(request.userType() == UserType.ADMIN ? Role.ADMIN : Role.APPLICANT);
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setProfileCompleted(false);
        user.setEmailVerifiedAt(Instant.now());

        if (request.userType() == UserType.ORGANIZATION) {
            user.attachOrganization(organizationService.createMinimalOrganization(user, request.organizationDisplayName()));
        }

        User savedUser = userRepository.save(user);
        organizationService.linkContactsToUserIfPossible(savedUser);

        return new RegisterResponse(
                "Registration completed successfully. These account details identify you in all future BOCRA interactions.",
                userService.toUserProfileResponse(savedUser)
        );
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        String identifier = request.identifier().trim();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(identifier, request.password()));

        User user = userRepository.findWithProfileByIdentifier(identifier)
                .orElseThrow(() -> new BadRequestException("User account not found."));

        user.setLastLoginAt(Instant.now());
        organizationService.linkContactsToUserIfPossible(user);
        return buildLoginResponse(user);
    }

    @Transactional
    public LoginResponse refresh(RefreshTokenRequest request) {
        RefreshTokenService.IssuedRefreshToken replacement;
        var existingToken = refreshTokenService.requireActive(request.refreshToken());
        User user = userRepository.findWithProfileByUuid(existingToken.getUser().getUuid())
                .orElseThrow(() -> new BadRequestException("User account not found."));

        TokenBundle tokenBundle = issueTokenBundle(user);
        replacement = tokenBundle.refreshToken();
        refreshTokenService.revokeAndReplace(existingToken, replacement);
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
        if (request.userType() == null) {
            throw new BadRequestException("User type is required.");
        }

        if (request.userType() == UserType.ORGANIZATION && !StringUtils.hasText(request.organizationDisplayName())) {
            throw new BadRequestException("Organization name is required for organization accounts.");
        }

        if (request.userType() != UserType.ORGANIZATION && StringUtils.hasText(request.organizationDisplayName())) {
            throw new BadRequestException("Organization name can only be provided for organization accounts.");
        }
    }

    private LoginResponse buildLoginResponse(User user) {
        return issueTokenBundle(user).response();
    }

    private TokenBundle issueTokenBundle(User user) {
        UserProfileResponse userProfileResponse = userService.toUserProfileResponse(user);
        String accessToken = jwtService.generateToken(SecurityUser.from(user));
        RefreshTokenService.IssuedRefreshToken refreshToken = refreshTokenService.issue(user, jwtService.refreshTokenExpirationMs());

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
