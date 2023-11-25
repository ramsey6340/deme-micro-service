package com.infinity.servicecause.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Admin {
    private String adminId;

    @NotNull(message = "email ne doit pas être null")
    private String email;

    @NotNull(message = "fullName ne doit pas être null")
    private String fullName;

    @NotNull(message = "profile ne doit pas être null")
    private String profile; // Le profile de l'utilisateur (user, organization, admin, adminRoot)

    private String creationDate = LocalDate.now().toString(); // Date de création du compte de l'utilisateur

}
