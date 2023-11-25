package com.infinity.serviceorganization.controllers;

import com.infinity.serviceorganization.models.Organization;
import com.infinity.serviceorganization.services.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/organizations/")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @GetMapping("")
    @Operation(summary = "Récuperer la liste des organisations")
    public List<Organization> getAllOrganizations() {
        return organizationService.getAllOrganizations();
    }

    @Operation(summary = "Récuperer une seule organisation")
    @GetMapping("{organisationId}")
    public ResponseEntity<Organization> getOrganizationById(@PathVariable String organisationId) throws ExecutionException, InterruptedException {
        return organizationService.getOrganizationById(organisationId);
    }

    @Operation(summary = "Modifier les données de l'organisation")
    @PatchMapping(value = "{organisationId}")
    public ResponseEntity<Organization> patchOrganizationInfo(@PathVariable String organisationId, @RequestBody Map<String, Object> userPatchInfo) throws ExecutionException, InterruptedException {
        return organizationService.patchOrganizationInfo(organisationId, userPatchInfo);
    }

    @Operation(summary = "Verifier si le login est déjà utilisé")
    @GetMapping("login/{login}")
    public ResponseEntity<Boolean> isLoginAvailable(@PathVariable String login) {
        return organizationService.isLoginAvailable(login);
    }

    @Operation(summary = "Mettre à jour le mot de passe de l'organisation")
    @PatchMapping(value = "reset-password/{userId}", params = "newPassword")
    public ResponseEntity<String> resetPassword(@PathVariable String userId, @RequestParam String newPassword) {
        return organizationService.resetPassword(userId, newPassword);
    }
}
