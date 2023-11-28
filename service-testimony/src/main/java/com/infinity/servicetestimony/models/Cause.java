package com.infinity.servicetestimony.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Cause {
    private String causeId;

    @NotNull(message = "Le name ne doit pas être null")
    private String name;

    @NotNull(message = "L'image ne doit pas être null")
    private String imageUrl;

    private String description;

    private String adminId;
    private boolean deleted; // pour savoir si la cause est supprimé ou non
}
