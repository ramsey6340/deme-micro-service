package com.infinity.servicedonation.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FinancialDonation extends Donation{

    private double amount; // Montant de la donation

    @NotNull(message = "methodPayment ne doit pas être null")
    private String methodPaymentId; // Le methode de paiement utilisé
}
