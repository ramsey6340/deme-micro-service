package com.infinity.serviceorganization.models;

import lombok.Data;

@Data
public class Address {
    private String addressId;

    private String country;

    private String city;

    private String neighborhood;
}
