package com.infinity.servicetestimony.controllers;

import com.infinity.servicetestimony.models.Testimony;
import com.infinity.servicetestimony.services.TestimonyService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/testimonies/")
public class TestimonyController {

    @Autowired
    private TestimonyService testimonyService;

    @GetMapping("")
    @Operation(summary = "Récuperer la liste de tous les témoignage")
    public List<Testimony> getAllTestimonies() {
        return testimonyService.getAllTestimonies();
    }

    @GetMapping("{testimonyId}")
    @Operation(summary = "Récuper un temoignage par son ID")
    public ResponseEntity<Testimony> getTestimonyById(@PathVariable String testimonyId) throws InterruptedException {
        return testimonyService.getTestimonyById(testimonyId);
    }

    @PatchMapping("{testimonyId}")
    @Operation(summary = "Modifier un témoignage spécifique")
    public ResponseEntity<Testimony> patchTestimonyInfo(@PathVariable String testimonyId, @RequestBody Map<String, Object> testimonyPatchInfo) {
        return testimonyService.patchTestimonyInfo(testimonyId, testimonyPatchInfo);
    }

    @PostMapping("organizations/{organizationId}")
    @Operation(summary = "Créer un nouveau temoignage par une  organisation")
    public ResponseEntity<String> createTestimonyByOrganization(@PathVariable String organizationId,
                                                                @Valid @RequestBody Testimony testimony)  {
        return testimonyService.createTestimonyByOrganization(organizationId, testimony);
    }

    @PostMapping("users/{userId}")
        @Operation(summary = "Créer un nouveau temoignage par un user simple")
        public ResponseEntity<String> createTestimonyByUser(@PathVariable String userId,
                                                            @Valid @RequestBody Testimony testimony)  {
            return testimonyService.createTestimonyByUser(userId, testimony);
        }

    @DeleteMapping("organizations/{organizationId}/{testimonyId}")
    @Operation(summary = "Supprimer un temoignage par une organisation")
    public ResponseEntity<String> deleteTestimonyByOrganization(@PathVariable String organizationId, @PathVariable String testimonyId) throws InterruptedException {
        return testimonyService.deleteTestimonyByOrganization(organizationId, testimonyId);
    }

    @DeleteMapping("users/{userId}/{testimonyId}")
    @Operation(summary = "Supprimer un temoignage par un user simple")
    public ResponseEntity<String> deleteTestimonyByUser(@PathVariable String userId, @PathVariable String testimonyId) throws InterruptedException {
        return testimonyService.deleteTestimonyByUser(userId, testimonyId);
    }
}
