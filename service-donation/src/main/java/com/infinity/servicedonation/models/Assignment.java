package com.infinity.servicedonation.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Assignment {
    private String assignmentId;

    @NotNull(message = "title ne doit pas être null")
    private String title;

    private String description;

    private String descriptionNeeds;

    private String descriptionResources;

    private String creationDate = LocalDate.now().toString(); // la date de création de la mission dans la base de donnée

    private String causeId;

    private String organizationId;

    private boolean deleted = false; // si la mission est supprimée

}
