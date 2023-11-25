package com.infinity.servicedonation.controllers;

import com.infinity.servicedonation.models.FinancialDonation;
import com.infinity.servicedonation.models.MaterialDonation;
import com.infinity.servicedonation.services.GiveDonationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @Operation(summary = "Une organisation va faire un don financier à une demande")
    public ResponseEntity<FinancialDonation> createFinancialDonationByUserToDemand(
            @PathVariable String userId,
            @PathVariable String beneficiaryId,
            @Valid @RequestBody FinancialDonation financialDonation) {
        return giveDonationService.createFinancialDonationByUserToDemand(
                userId, beneficiaryId, financialDonation);
    }


    /*===================================Donation Materiel==============================*/
    @PostMapping("material/organizations/{organizationId}/to/organizations/{beneficiaryId}")
    @Operation(summary = "Une organisation va faire un don materiel à une organisation")
    public ResponseEntity<MaterialDonation> createMaterialDonationByOrganizationToOrganization(
            @PathVariable String organizationId,
            @PathVariable String beneficiaryId,
            @Valid @RequestBody FinancialDonation financialDonation) {
        return giveDonationService.createMaterialDonationByOrganizationToOrganization(
                organizationId, beneficiaryId, financialDonation);
    }

    @PostMapping("material/organizations/{organizationId}/to/demands/{beneficiaryId}")
    @Operation(summary = "Une organisation va faire un don materiel à une demande")
    public ResponseEntity<MaterialDonation> createMaterialDonationByOrganizationToDemand(
            @PathVariable String organizationId,
            @PathVariable String beneficiaryId,
            @Valid @RequestBody FinancialDonation financialDonation) {
        return giveDonationService.createMaterialDonationByOrganizationToDemand(
                organizationId, beneficiaryId, financialDonation);
    }


    @PostMapping("material/users/{userId}/to/organizations/{beneficiaryId}")
    @Operation(summary = "Un user va faire un don materiel à une organisation")
    public ResponseEntity<MaterialDonation> createMaterialDonationByUserToOrganization(
            @PathVariable String userId,
            @PathVariable String beneficiaryId,
            @Valid @RequestBody FinancialDonation financialDonation) {
        return giveDonationService.createMaterialDonationByUserToOrganization(
                userId, beneficiaryId, financialDonation);
    }

    @PostMapping("material/users/{userId}/to/demands/{beneficiaryId}")
    @Operation(summary = "Une organisation va faire un don materiel à une demande")
    public ResponseEntity<MaterialDonation> createMaterialDonationByUserToDemand(
            @PathVariable String userId,
            @PathVariable String beneficiaryId,
            @Valid @RequestBody FinancialDonation financialDonation) {
        return giveDonationService.createMaterialDonationByUserToDemand(
                userId, beneficiaryId, financialDonation);
    }


}
