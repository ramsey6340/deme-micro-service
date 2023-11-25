package com.infinity.serviceactivity.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Activity {
    private String activityId;

    @NotNull(message = "title ne doit pas être null")
    private String title;

    @NotNull(message = "startDate ne doit pas être null")
    private String startDate;

    private String endDate;

    private String description;

    @NotNull(message = "assignmentId ne doit pas être null")
    private String assignmentId;

    private String creationDate = LocalDate.now().toString(); // la date de création de l'activité dans la base de donnée
}
