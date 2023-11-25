package com.infinity.serviceadmin.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Admin {
    private String adminId;

    @NotNull(message = "email ne doit pas être null")
    private String email;

    @NotNull(message = "fullName ne doit pas être null")
    private String fullName;

    @NotNull(message = "profile ne doit pas être null")
    private String profile; // Le profile de l'utilisateur (user, organization, admin, adminRoot)

}
