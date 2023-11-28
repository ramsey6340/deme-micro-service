package com.infinity.serviceactivity.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class Post {
    private String postId;

    private List<String> imageUrls = new ArrayList<>();

    @NotNull(message = "message ne doit pas être null")
    private String message;

    private String videoUrl;

    private String creationDate = LocalDate.now().toString(); // la date de création du post dans la base de donnée

    private String activityId;

    private boolean deleted = false; // si le post est supprimé

}
