package com.infinity.servicedonation.controllers;

import com.infinity.servicedonation.models.Demand;
import com.infinity.servicedonation.services.DemandService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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

    @PostMapping(value = "users/{userId}", params = {"causeId", "guarantorId"})
    @Operation(summary = "Crée une nouvelle demande par un user")
    public ResponseEntity<String> createDemandByUser(@PathVariable String userId,
                                                     @RequestParam("causeId") String causeId,
                                                     @RequestParam("guarantorId") String guarantorId,
                                                     @Valid @RequestBody Demand demand) {
        return demandService.createDemandByUser(userId, causeId, guarantorId, demand);
    }

    @PostMapping(value = "organizations/{organizationId}", params = "assignmentId")
    @Operation(summary = "Crée une nouvelle demande par une organisation")
    public ResponseEntity<String> createDemandByOrganization(@PathVariable String organizationId,
                                                             @RequestParam String  assignmentId,
                                                             @Valid @RequestBody Demand demand) {
        return demandService.createDemandByOrganization(organizationId, assignmentId, demand);
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

    @PatchMapping("organizations/{organizationId}/demands/{demandId}")
    @Operation(summary = "Mette à jour une demande")
    public ResponseEntity<Demand> patchDemandInfoByOrganization(@PathVariable String organizationId,
                                                                @PathVariable String demandId,
                                                                @RequestBody Map<String, Object> demandPatchInfo) {
        return demandService.patchDemandInfoByOrganization(organizationId, demandId, demandPatchInfo);
    }

    @PatchMapping("users/{userId}/demands/{demandId}")
    @Operation(summary = "Mette à jour une demande")
    public ResponseEntity<Demand> patchDemandInfoByUser(@PathVariable String userId,
                                                        @PathVariable String demandId,
                                                        @RequestBody Map<String, Object> demandPatchInfo) {
        return demandService.patchDemandInfoByUser(userId, demandId, demandPatchInfo);
    }

    @DeleteMapping("organizations/{organizationId}/{demandId}")
    @Operation(summary = "Supprimer un temoignage par une organisation")
    public ResponseEntity<String> deleteDemandByOrganization(@PathVariable String organizationId,
                                                             @PathVariable String demandId) throws InterruptedException {
        return demandService.deleteDemandByOrganization(organizationId, demandId);
    }

    @DeleteMapping("users/{userId}/{demandId}")
    @Operation(summary = "Supprimer un temoignage par un user simple")
    public ResponseEntity<String> deleteDemandByUser(@PathVariable String userId,
                                                     @PathVariable String demandId) throws InterruptedException {
        return demandService.deleteDemandByUser(userId, demandId);
    }

    @GetMapping("users/{userId}/demands")
    @Operation(summary = "Récuperer les demandes d'un user")
    public List<Demand> getAllDemandsForUser(@PathVariable String userId) {
        return demandService.getAllDemandsForUser(userId);
    }

    @GetMapping("organizations/{organizationId}/demands")
    @Operation(summary = "Récuperer les demandes d'une organisation")
    public List<Demand> getAllDemandsForOrganization(@PathVariable String organizationId) {
        return demandService.getAllDemandsForOrganization(organizationId);
    }
}
