package com.infinity.servicedonation.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class Organization {
    /**
     * Organization réprésente les organisations humanitaires dans l'application Deme,
     */
    private String organizationId;
    private boolean activated=true; // permet de savoir si l'utilisateur à accès à l'application

    @NotNull(message = "{NotNull.user.name}")
    private String name; // Nom complet de l'utilisateur

    @NotNull(message = "{NotNull.user.email}")
    private String email;

    private String login;

    private String numTel; // Numéro de téléphone de l'organisation

    private String startDateExercise; // Date de naissance de l'organisation

    private String imageUrl; // Image de profil de l'organisation

    private String deviceType; // Type de l'appareil sur lequel l'organisation a créer un compte

    private boolean anonymous; // Permet de savoir si l'organisation veut rester anonyme sur les donations qu'il fait

    private String creationDate = LocalDate.now().toString(); // Date de création du compte de l'utilisateur

    private boolean deleted; // Permet de savoir si l'organisation est supprimé ou pas

    private List<String> preferredPaymentMethods = new ArrayList<>(); // Liste des moyens de paiement préférés de l'organisation

    private List<String> favoriteHumanitarianCauses = new ArrayList<>(); // Liste des causes humanitaires soutenue par l'organisation

    @NotNull(message = "profile ne doit pas être null")
    private String profile; // Le profile de l'utilisateur (user, organization, admin, adminRoot)

    private String matricule; // Matricule de l'organisation
    private double nbSubscription; // Nombre d'abonnement

    private boolean valid; // Pour savoir si l'organisation est valide auprès du système DEME

    private boolean verified; // Pour savoir si l'organisation est verifié auprès de l'Etat

    private String type; // Le type de l'organisation (Association, ONG, Fondation, etc)

    private List<String> subscribersId = new ArrayList<>(); // Liste des abonnées à l'organisation

}
