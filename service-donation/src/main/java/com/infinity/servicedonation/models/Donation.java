package com.infinity.servicedonation.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public abstract class Donation {
    private String donationId;
    private boolean used=false; // pour savoir si la donation a été utilisé dans une activité
    private String creationDate = LocalDate.now().toString();
    private String donorUserId; // Le user qui a fait le don
    private String donorOrganizationId; // L'organisation qui a fait le don

    private String beneficiaryOrganizationId;  // L'organisation qui va beneficier de la donation
    private String beneficiaryDemandId; // La demande qui va beneficier de la demande
    private boolean deleted=false; // si la donation a été supprimé ou pas
}
