package com.infinity.serviceadmin.controllers;

import com.infinity.serviceadmin.models.Admin;
import com.infinity.serviceadmin.services.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/admins/")
public class AdminControllers {

    @Autowired
    private AdminService adminService;

    @Operation(summary = "Récuperer un admin par son ID")
    @GetMapping("{adminId}")
    public ResponseEntity<Admin> getAdminById(@PathVariable String adminId) throws ExecutionException, InterruptedException {
        return adminService.getAdminById(adminId);
    }
}
