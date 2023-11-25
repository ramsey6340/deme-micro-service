package com.infinity.servicedonation.controllers;

import com.infinity.servicedonation.models.Demand;
import com.infinity.servicedonation.services.DemandService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/donations/demands/")
public class DemandController {

    @Autowired
    private DemandService demandService;

    @PostMapping("users/{userId}")
    @Operation(summary = "Crée une nouvelle demande par un user")
    public ResponseEntity<String> createDemandByUser(@PathVariable String userId, @RequestBody Demand demand) {
        return demandService.createDemandByUser(userId, demand);
    }

        @PostMapping("organizations/{organizationId}")
    @Operation(summary = "Crée une nouvelle demande par une organisation")
    public ResponseEntity<String> createDemandByOrganization(@PathVariable String organizationId, @RequestBody Demand demand) {
        return demandService.createDemandByOrganization(organizationId, demand);
    }

    @GetMapping("")
    @Operation(summary = "Récuperer la liste des demandes")
    public List<Demand> getAllDemand() {
        return demandService.getAllDemands();
    }

    @GetMapping("{demandId}")
    @Operation(summary = "Récuperer une demande precise")
    public ResponseEntity<Demand> getDemandById(@PathVariable String demandId) throws InterruptedException {
        return demandService.getDemandById(demandId);
    }

    @PatchMapping("{demandId}")
    @Operation(summary = "Mette à jour une demande")
    public ResponseEntity<Demand> patchDemandInfo(@PathVariable String demandId, @RequestBody Map<String, Object> demandPatchInfo) {
        return demandService.patchDemandInfo(demandId, demandPatchInfo);
    }

    @DeleteMapping("organizations/{organizationId}/{testimonyId}")
    @Operation(summary = "Supprimer un temoignage par une organisation")
    public ResponseEntity<String> deleteDemandByOrganization(@PathVariable String organizationId, @PathVariable String testimonyId) throws InterruptedException {
        return demandService.deleteDemandByOrganization(organizationId, testimonyId);
    }

    @DeleteMapping("users/{userId}/{testimonyId}")
    @Operation(summary = "Supprimer un temoignage par un user simple")
    public ResponseEntity<String> deleteDemandByUser(@PathVariable String userId, @PathVariable String testimonyId) throws InterruptedException {
        return demandService.deleteDemandByUser(userId, testimonyId);
    }
}
