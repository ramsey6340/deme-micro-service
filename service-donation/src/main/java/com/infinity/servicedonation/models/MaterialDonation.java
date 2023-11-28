package com.infinity.servicedonation.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MaterialDonation extends Donation{
    @NotNull(message = "description ne doit pas être null")
    private String descriptionMaterialDonation;
    private String imageUrl;
}
