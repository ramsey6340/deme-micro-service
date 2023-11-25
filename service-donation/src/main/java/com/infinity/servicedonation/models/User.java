package com.infinity.servicedonation.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//import java.time.LocalDate;

@Data
public class User {
    /**
     * User réprésente les utilisateurs simple dans l'application Deme
     */
    private String userId;
    private boolean isActivated=true; // permet de savoir si l'utilisateur à accès à l'application

    @NotNull(message = "{NotNull.user.name}")
    private String name; // Nom complet de l'utilisateur

    @NotNull(message = "{NotNull.user.email}")
    private String email;

    private String login;

    private String numTel; // Numéro de téléphone de l'utilisateur

    private String birthDay; // Date de naissance de l'utilisateur

    private String imageUrl; // Image de profil de l'utilisateur

    private String deviceType; // Type de l'appareil sur lequel l'utilisateur a créer un compte

    private boolean isAnonymous; // Permet de savoir si l'utilisateur veut rester anonyme sur les donations qu'il fait

    private String creationDate = LocalDate.now().toString(); // Date de création du compte de l'utilisateur

    private boolean delete; // Permet de savoir si l'utilisateur est supprimé ou pas

    private List<String> preferredPaymentMethods = new ArrayList<>(); // Liste des moyens de paiement préférés de l'utilisateur

    private List<String> favoriteHumanitarianCauses = new ArrayList<>(); // Liste des causes humanitaires préférées de l'utilisateur

    @NotNull(message = "profile ne doit pas être null")
    private String profile; // Le profile de l'utilisateur (user, organization, admin, adminRoot)

    private String gender;
}
