package com.infinity.servicedonation.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Demand {
    private String demandId;
    private String description;
    private String videoUrl;
    private String imageUrl;
    private boolean active=true;
    private String creationDate = LocalDate.now().toString();

    @NotNull(message = "causeId ne doit pas être null")
    private String causeId;
    @NotNull(message = "guarantorId ne doit pas être null")
    private String guarantorId; // L'organisation qui est garant de la demande

    private String userId; // Le user qui a fait la demande
    private String organizationId; // L'organisation qui a fait la demande
}
