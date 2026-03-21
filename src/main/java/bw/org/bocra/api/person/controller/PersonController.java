/*
 * Exposes authenticated BOCRA person profile retrieval and update endpoints.
 */
package bw.org.bocra.api.person.controller;

import bw.org.bocra.api.common.dto.ApiResponse;
import bw.org.bocra.api.person.dto.PersonProfileResponse;
import bw.org.bocra.api.person.dto.UpdatePersonProfileRequest;
import bw.org.bocra.api.person.service.PersonService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/persons")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<PersonProfileResponse>> getCurrentProfile() {
        return ResponseEntity.ok(ApiResponse.success("Current person profile retrieved successfully.", personService.getCurrentProfile()));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<PersonProfileResponse>> updateCurrentProfile(
            @Valid @RequestBody UpdatePersonProfileRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Person profile updated successfully.", personService.updateCurrentProfile(request)));
    }
}
