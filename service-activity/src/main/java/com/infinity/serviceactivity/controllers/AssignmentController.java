package com.infinity.serviceactivity.controllers;

import com.infinity.serviceactivity.models.Activity;
import com.infinity.serviceactivity.models.Assignment;
import com.infinity.serviceactivity.services.AssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/activities/assignments/")
public class AssignmentController {

    @Autowired
    AssignmentService assignmentService;

    @Operation(summary = "Récuperer tous les mission des organisations")
    @GetMapping("")
    public List<Assignment> getAllAssignments() {
        return assignmentService.getAllAssignments();
    }

    @Operation(summary = "Récuperer tous les mission d'une organisation")
    @GetMapping("organizations/{organizationId}")
    public List<Assignment> getAllAssignmentsByOrganizationId(@PathVariable String organizationId) {
        return assignmentService.getAllAssignmentsByOrganizationId(organizationId);
    }

    @Operation(summary = "Récuperer une mission par son ID")
    @GetMapping("{assignmentId}")
    public ResponseEntity<Assignment> getAssignmentById(@PathVariable String assignmentId) throws InterruptedException {
        return assignmentService.getAssignmentById(assignmentId);
    }

    @Operation(summary = "Modifier les données d'une mission")
    @PatchMapping(value = "{assignmentId}")
    public ResponseEntity<Assignment> patchActivityInfo(@PathVariable String assignmentId, @RequestBody Map<String, Object> assignmentPatchInfo) throws ExecutionException, InterruptedException {
        return assignmentService.patchAssignmentInfo(assignmentId, assignmentPatchInfo);
    }

}
