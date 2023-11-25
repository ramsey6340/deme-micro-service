package com.infinity.servicedonation.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MethodPayment {
    private String methodPaymentId;

    @NotNull(message = "name ne doit pas etre null")
    private String name;

    private String description;

    @NotNull(message = "image ne doit pas etre null")
    private String imageUrl;

    private String termsOfUse;


}
