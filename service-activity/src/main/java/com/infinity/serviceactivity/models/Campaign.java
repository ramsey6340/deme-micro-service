package com.infinity.serviceactivity.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Campaign {
    private String campaignId;

    @NotNull(message = "title ne peut pas être null")
    private String title;

    private String videoUrl;

    private String description;

    private String creationDate = LocalDate.now().toString(); // la date de création de la campagne de sensibiisation dans la base de données

    private String assignmentId; // la mission concerné par la campagne

    private boolean deleted = false; // si la campagne est supprimée
}
