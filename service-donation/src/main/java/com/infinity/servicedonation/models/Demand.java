package com.infinity.servicedonation.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Demand {
    private String demandId;
    @NotNull(message = "description ne doit pas être null")
    private String description;
    private String videoUrl;
    private String imageUrl;
    private boolean accepted=false; // pour savoir si l'organisation accepte d'ête le garant de la demande
    private boolean active=true;
    private String creationDate = LocalDate.now().toString();
    private boolean deleted=false;

    private String causeId;
    private String guarantorId; // L'organisation qui est garant de la demande

    private String userId; // Le user qui a fait la demande
    private String organizationId; // L'organisation qui a fait la demande
}
