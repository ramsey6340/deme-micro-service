package com.infinity.serviceactivity.controllers;

import com.infinity.serviceactivity.models.Assignment;
import com.infinity.serviceactivity.models.Campaign;
import com.infinity.serviceactivity.services.CampaignService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/activities/campaigns/")
public class CampaignController {

    @Autowired
    private CampaignService campaignService;

    @PostMapping(value = "organizations/{organizationId}/campaigns/", params = {"assignmentId"})
    @Operation(summary = "Ajouter une campagne")
    public ResponseEntity<String> createCampaign(@PathVariable String organizationId,
                                                 @RequestParam String assignmentId, @RequestBody Campaign campaign) {
        return campaignService.createCampaign(organizationId, assignmentId, campaign);
    }

    @GetMapping(value = "organizations/{organizationId}/campaigns/")
    @Operation(summary = "Récupérer les campagnes d'une organisation")
    public List<Campaign> getAllCampaignsForOrganization(@PathVariable String organizationId) {
        return campaignService.getAllCampaignsForOrganization(organizationId);
    }

    @GetMapping("")
    @Operation(summary = "Récupérer tous les campagnes")
    public List<Campaign> getAllCampaigns() {
        return campaignService.getAllCampaigns();
    }

    @GetMapping("{campaignId}")
    @Operation(summary = "Récupérer une campagne par son ID")
    public ResponseEntity<Campaign> getCampaignById(@PathVariable String campaignId) {
        return campaignService.getCampaignById(campaignId);
    }

    @Operation(summary = "Modifier les données d'une campagne")
    @PatchMapping("organizations/{organizationId}/campaigns/{campaignId}")
    public ResponseEntity<Campaign> patchActivityInfo(@PathVariable String organizationId, @PathVariable String campaignId, @RequestBody Map<String, Object> assignmentPatchInfo) throws ExecutionException, InterruptedException {
        return campaignService.patchCampaignInfo(organizationId, campaignId, assignmentPatchInfo);
    }

    @DeleteMapping("organizations/{organizationId}/campaigns/{campaignId}")
    @Operation(summary = "Supprimer une activité")
    public ResponseEntity<String> deleteCampaign(@PathVariable String organizationId, @PathVariable String campaignId) {
        return campaignService.deleteCampaign(organizationId, campaignId);
    }


}
