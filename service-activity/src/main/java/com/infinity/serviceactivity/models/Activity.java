package com.infinity.serviceactivity.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import javax.annotation.RegEx;
import java.time.LocalDate;

@Data
public class Activity {
    private String activityId;

    @NotNull(message = "title ne doit pas être null")
    private String title;

    @NotNull(message = "startDate ne doit pas être null")
    @Pattern(regexp = "([0-9]{4})-([0-9]{2})-([0-9]{2})", message = "Le format de startDate doit être yyyy-MM-dd")
    private String startDate; // date de début de l'activité dans la base de données. Ex: 2021-01-01

    private String endDate;

    private String description;

    private String assignmentId;

    private String creationDate = LocalDate.now().toString(); // la date de création de l'activité dans la base de donnée

    private boolean deleted = false; // si l'activité est supprimée
}
