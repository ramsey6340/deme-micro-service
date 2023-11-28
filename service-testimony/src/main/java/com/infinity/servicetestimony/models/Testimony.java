package com.infinity.servicetestimony.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class Testimony {
    private String testimonyId;
    private String message;
    private String imageUrl;
    private String videoUrl;
    private String creationDate = LocalDate.now().toString();
    private String userId;
    private String organizationId;
    private boolean deleted; // pour savoir si le temoignage est supprimé ou non

    @NotNull(message = "causeId ne doit pas être null")
    private String causeId;
}
