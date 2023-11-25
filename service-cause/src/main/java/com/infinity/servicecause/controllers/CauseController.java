package com.infinity.servicecause.controllers;

import com.infinity.servicecause.models.Cause;
import com.infinity.servicecause.services.CauseService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/causes/")
public class CauseController {

    @Autowired
    private CauseService causeService;

    @Operation(summary = "Crée une nouvelle cause")
    @PostMapping("{adminId}")
    ResponseEntity<Cause> createCause(@PathVariable String adminId, @Valid @RequestBody Cause cause) {
        return causeService.createCause(adminId, cause);
    }

    @Operation(summary = "Récuperer la liste des causes")
    @GetMapping("")
    List<Cause> getAllCauses() {
        return causeService.getAllCauses();
    }

    @Operation(summary = "Récuperer une cause par son ID")
    @GetMapping("{causeId}")
    public ResponseEntity<Cause> getCauseById(@PathVariable  String causeId) throws InterruptedException {
        return causeService.getCauseById(causeId);
    }
}
