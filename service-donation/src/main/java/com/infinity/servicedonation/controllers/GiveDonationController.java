package com.infinity.servicedonation.controllers;

import com.infinity.servicedonation.models.FinancialDonation;
import com.infinity.servicedonation.models.MaterialDonation;
import com.infinity.servicedonation.services.GiveDonationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/donations/")
public class GiveDonationController {

    @Autowired
    private GiveDonationService giveDonationService;

    @PostMapping("financial/organizations/{organizationId}/to/organizations/{beneficiaryId}")
    @Operation(summary = "Une organisation va faire un don financier à une organisation")
    public ResponseEntity<FinancialDonation> createFinancialDonationByOrganizationToOrganization(
            @PathVariable String organizationId,
            @PathVariable String beneficiaryId,
            @Valid @RequestBody FinancialDonation financialDonation) {
        return giveDonationService.createFinancialDonationByOrganizationToOrganization(organizationId, beneficiaryId, financialDonation);
    }

    @PostMapping("financial/organizations/{organizationId}/to/demands/{beneficiaryId}")
    @Operation(summary = "Une organisation va faire un don financier à une demande")
    public ResponseEntity<FinancialDonation> createFinancialDonationByOrganizationToDemand(
            @PathVariable String organizationId,
            @PathVariable String beneficiaryId,
            @Valid @RequestBody FinancialDonation financialDonation) {
        return giveDonationService.createFinancialDonationByOrganizationToDemand(
                organizationId, beneficiaryId, financialDonation);
    }


    @PostMapping("financial/users/{userId}/to/organizations/{beneficiaryId}")
    @Operation(summary = "Un user va faire un don financier à une organisation")
    public ResponseEntity<FinancialDonation> createFinancialDonationByUserToOrganization(
            @PathVariable String userId,
            @PathVariable String beneficiaryId,
            @Valid @RequestBody FinancialDonation financialDonation) {
        return giveDonationService.createFinancialDonationByUserToOrganization(userId, beneficiaryId, financialDonation);
    }

    @PostMapping("financial/users/{userId}/to/demands/{beneficiaryId}")
    @Operation(summary = "Un user va faire un don financier à une demande")
    public ResponseEntity<FinancialDonation> createFinancialDonationByUserToDemand(
            @PathVariable String userId,
            @PathVariable String beneficiaryId,
            @Valid @RequestBody FinancialDonation financialDonation) {
        return giveDonationService.createFinancialDonationByUserToDemand(
                userId, beneficiaryId, financialDonation);
    }

    @GetMapping("list-donation-financial")
    @Operation(summary = "Récuperer la liste des donation financier")
    public List<FinancialDonation> getAllFinancialDonation() {
        return giveDonationService.getAllFinancialDonation();
    }

    @GetMapping("list-donation-material")
    @Operation(summary = "Récuperer la liste des donation materiel")
    public List<MaterialDonation> getAllMaterialDonation() {
        return giveDonationService.getAllMaterialDonation();
    }

    // =========== Donation faite

    @GetMapping("list-donation-material-made/users/{userId}")
    @Operation(summary = "Récuperer la liste des donation materiel fait par un user")
    public List<MaterialDonation> getAllMaterialDonationMadeByUser(@PathVariable String userId) {
        return giveDonationService.getAllMaterialDonationMadeByUser(userId);
    }

    @GetMapping("list-donation-material-made/organizations/{organizationId}")
    @Operation(summary = "Récuperer la liste des donation materiel fait par une organisation")
    public List<MaterialDonation> getAllMaterialDonationMadeByOrganization(@PathVariable String organizationId) {
        return giveDonationService.getAllMaterialDonationMadeByOrganization(organizationId);
    }

    @GetMapping("list-donation-financial-made/users/{userId}")
    @Operation(summary = "Récuperer la liste des donation financier fait par un user")
    public List<FinancialDonation> getAllFinancialDonationMadeByUser(@PathVariable String userId) {
        return giveDonationService.getAllFinancialDonationMadeByUser(userId);
    }

    @GetMapping("list-donation-financial-made/organizations/{organizationId}")
    @Operation(summary = "Récuperer la liste des donation finacier fait par une organisation")
    public List<FinancialDonation> getAllFinancialDonationMadeByOrganization(@PathVariable String organizationId) {
        return giveDonationService.getAllFinancialDonationMadeByOrganization(organizationId);
    }

    // =========== Donation reçu

    @GetMapping("list-donation-material-received/demands/{demandId}")
    @Operation(summary = "Récuperer la liste des donation materiel reçu par une demande")
    public List<MaterialDonation> getAllMaterialDonationReceivedByDemand(@PathVariable String demandId) {
        return giveDonationService.getAllMaterialDonationReceivedByDemand(demandId);
    }

    @GetMapping("list-donation-material-received/organizations/{organizationId}")
    @Operation(summary = "Récuperer la liste des donation materiel reçu par une organisation")
    public List<MaterialDonation> getAllMaterialDonationReceivedByOrganization(@PathVariable String organizationId) {
        return giveDonationService.getAllMaterialDonationReceivedByOrganization(organizationId);
    }

    @GetMapping("list-donation-financial-received/demands/{demandId}")
    @Operation(summary = "Récuperer la liste des donation financier reçu par un user")
    public List<FinancialDonation> getAllFinancialDonationForDemand(@PathVariable String demandId) {
        return giveDonationService.getAllFinancialDonationReceivedByDemand(demandId);
    }

    @GetMapping("list-donation-financial-received/organizations/{organizationId}")
    @Operation(summary = "Récuperer la liste des donation finacier reçu par une organisation")
    public List<FinancialDonation> getAllFinancialDonationForOrganization(@PathVariable String organizationId) {
        return giveDonationService.getAllFinancialDonationReceivedByOrganization(organizationId);
    }

    // ========= Fin des donatins reçu



    @PostMapping("material/organizations/{organizationId}/to/organizations/{beneficiaryId}")
    @Operation(summary = "Une organisation va faire un don materiel à une organisation")
    public ResponseEntity<MaterialDonation> createMaterialDonationByOrganizationToOrganization(
            @PathVariable String organizationId,
            @PathVariable String beneficiaryId,
            @Valid @RequestBody MaterialDonation materialDonation) {
        return giveDonationService.createMaterialDonationByOrganizationToOrganization(
                organizationId, beneficiaryId, materialDonation);
    }

    @PostMapping("material/organizations/{organizationId}/to/demands/{beneficiaryId}")
    @Operation(summary = "Une organisation va faire un don materiel à une demande")
    public ResponseEntity<MaterialDonation> createMaterialDonationByOrganizationToDemand(
            @PathVariable String organizationId,
            @PathVariable String beneficiaryId,
            @Valid @RequestBody MaterialDonation materialDonation) {
        return giveDonationService.createMaterialDonationByOrganizationToDemand(
                organizationId, beneficiaryId, materialDonation);
    }


    @PostMapping("material/users/{userId}/to/organizations/{beneficiaryId}")
    @Operation(summary = "Un user va faire un don materiel à une organisation")
    public ResponseEntity<MaterialDonation> createMaterialDonationByUserToOrganization(
            @PathVariable String userId,
            @PathVariable String beneficiaryId,
            @Valid @RequestBody MaterialDonation materialDonation) {
        return giveDonationService.createMaterialDonationByUserToOrganization(
                userId, beneficiaryId, materialDonation);
    }

    @PostMapping("material/users/{userId}/to/demands/{beneficiaryId}")
    @Operation(summary = "Une organisation va faire un don materiel à une demande")
    public ResponseEntity<MaterialDonation> createMaterialDonationByUserToDemand(
            @PathVariable String userId,
            @PathVariable String beneficiaryId,
            @Valid @RequestBody MaterialDonation materialDonation) {
        return giveDonationService.createMaterialDonationByUserToDemand(
                userId, beneficiaryId, materialDonation);
    }


}
