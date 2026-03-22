/*
 * Exposes organization profile and contact management endpoints for organization accounts.
 */
package bw.org.bocra.api.organization.controller;

import bw.org.bocra.api.common.dto.ApiResponse;
import bw.org.bocra.api.organization.dto.OrganizationContactResponse;
import bw.org.bocra.api.organization.dto.OrganizationProfileResponse;
import bw.org.bocra.api.organization.dto.UpdateOrganizationProfileRequest;
import bw.org.bocra.api.organization.dto.UpsertOrganizationContactRequest;
import bw.org.bocra.api.organization.service.OrganizationService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<OrganizationProfileResponse>> getCurrentOrganizationProfile() {
        return ResponseEntity.ok(ApiResponse.success(
                "Current organization profile retrieved successfully.",
                organizationService.getCurrentOrganizationProfile()
        ));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<OrganizationProfileResponse>> updateCurrentOrganizationProfile(
            @Valid @RequestBody UpdateOrganizationProfileRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Organization profile updated successfully.",
                organizationService.updateCurrentOrganizationProfile(request)
        ));
    }

    @GetMapping("/me/contacts")
    public ResponseEntity<ApiResponse<List<OrganizationContactResponse>>> listCurrentOrganizationContacts() {
        return ResponseEntity.ok(ApiResponse.success(
                "Organization contacts retrieved successfully.",
                organizationService.listCurrentOrganizationContacts()
        ));
    }

    @PostMapping("/me/contacts")
    public ResponseEntity<ApiResponse<OrganizationContactResponse>> addCurrentOrganizationContact(
            @Valid @RequestBody UpsertOrganizationContactRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Organization contact added successfully.",
                organizationService.addCurrentOrganizationContact(request)
        ));
    }

    @PutMapping("/me/contacts/{contactUuid}")
    public ResponseEntity<ApiResponse<OrganizationContactResponse>> updateCurrentOrganizationContact(
            @PathVariable UUID contactUuid,
            @Valid @RequestBody UpsertOrganizationContactRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Organization contact updated successfully.",
                organizationService.updateCurrentOrganizationContact(contactUuid, request)
        ));
    }
}
