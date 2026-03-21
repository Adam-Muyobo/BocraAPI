/*
 * Exposes authenticated and admin-focused BOCRA user account endpoints.
 */
package bw.org.bocra.api.user.controller;

import bw.org.bocra.api.common.dto.ApiResponse;
import bw.org.bocra.api.user.dto.UserProfileResponse;
import bw.org.bocra.api.user.service.UserService;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUserProfile() {
        return ResponseEntity.ok(ApiResponse.success("Current user profile retrieved successfully.", userService.getCurrentUserProfile()));
    }

    @GetMapping("/{userUuid}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile(@PathVariable UUID userUuid) {
        return ResponseEntity.ok(ApiResponse.success("User profile retrieved successfully.", userService.getUserProfile(userUuid)));
    }
}
