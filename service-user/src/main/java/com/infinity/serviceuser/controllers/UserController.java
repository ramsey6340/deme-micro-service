package com.infinity.serviceuser.controllers;

import com.infinity.serviceuser.models.User;
import com.infinity.serviceuser.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/users/")
public class UserController {

    @Autowired
    private UserService userService;


    @Operation(summary = "Modifier les données de l'utilisateur")
    @PatchMapping(value = "{userId}")
    public ResponseEntity<User> patchUserInfo(@PathVariable String userId, @RequestBody Map<String, Object> userPatchInfo) throws ExecutionException, InterruptedException {
        return userService.patchUserInfo(userId, userPatchInfo);
    }


    @Operation(summary = "Récuperer un seul utilisateur")
    @GetMapping("{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId) throws ExecutionException, InterruptedException {
        return userService.getUserById(userId);
    }

    @Operation(summary = "Récuperer la liste des utilisateurs")
    @GetMapping("")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Verifier si le login est déjà utilisé")
    @GetMapping("login/{login}")
    public ResponseEntity<Boolean> isLoginAvailable(@PathVariable String login) {
        return userService.isLoginAvailable(login);
    }


}
