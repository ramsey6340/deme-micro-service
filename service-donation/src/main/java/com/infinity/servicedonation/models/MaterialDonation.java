package com.infinity.servicedonation.models;

import lombok.Data;

@Data
public class MaterialDonation extends Donation{
    private String description;
    private String imageUrl;
}
