package com.infinity.serviceauth.controllers;

import com.google.firebase.auth.FirebaseAuthException;
import com.infinity.serviceauth.models.Organization;
import com.infinity.serviceauth.models.User;
import com.infinity.serviceauth.services.UserAuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/")
public class UserAuthController {

    @Autowired
    private UserAuthService userAuthService;

    @Operation(summary = "Verification de l'email")
    @GetMapping("verify-mail/{email}")
    public ResponseEntity<String> sendMailOtpCode(@PathVariable String email) {
        return userAuthService.sendMailOtpCode(email);
    }

    @PostMapping(value = "users", params = {"profile", "password"})
    @Operation(summary = "Créer un nouveau compte utilisateur")
    public ResponseEntity<String> createUser(@RequestParam("profile") String profile, @RequestParam("password") String password, @Valid @RequestBody User user){
        return userAuthService.createUser(profile, password, user);
    }

    @PostMapping(value = "organizations", params = {"profile", "password"})
    @Operation(summary = "Créer une nouvelle organisation")
    public ResponseEntity<String> createOrganization(@RequestParam("profile") String profile, @RequestParam("password") String password, @Valid @RequestBody Organization organization){
        return userAuthService.createOrganization(profile, password, organization);
    }


    @Operation(summary = "Recuperer un user par son email et mot de passe")
    @GetMapping(value = "users/email", params = {"email", "password"})
    public ResponseEntity<User> getUserByEmailAndPassword(@RequestParam("email") String email, @RequestParam("password") String password) {
        return userAuthService.getUserByEmailAndPassword(email, password);
    }

    @Operation(summary = "Recuperer un user par son numero de téléphone et mot de passe")
    @GetMapping(value = "users/numtel", params = {"numtel", "password"})
    public ResponseEntity<User> getUserByNumTelAndPassword(@RequestParam("numtel") String numTel, @RequestParam("password") String password) {
        return userAuthService.getUserByNumTelAndPassword(numTel, password);
    }

    @Operation(summary = "Verification de l'email de l'utilisateur pour la reinitialisation du mot de passe")
    @GetMapping("verify-reset-mail/{email}")
    public ResponseEntity<String> verifyEmailForResetPassword(@PathVariable String email) throws FirebaseAuthException {
        return userAuthService.verifyEmailForResetPassword(email);
    }

    @Operation(summary = "Mettre à jour le mot de passe de l'utilisateur")
    @PatchMapping(value = "reset-password/{userId}", params = "newPassword")
    public ResponseEntity<String> resetPassword(@PathVariable String userId, @RequestParam String newPassword) {
        return userAuthService.resetPassword(userId, newPassword);
    }
}
