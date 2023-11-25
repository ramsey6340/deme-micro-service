package com.infinity.serviceactivity.controllers;

import com.infinity.serviceactivity.models.Activity;
import com.infinity.serviceactivity.models.Post;
import com.infinity.serviceactivity.services.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/activities/activities/")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @Operation(summary = "Récuperer tous les activités")
    @GetMapping("")
    public List<Activity> getAllActivities() {
        return activityService.getAllActivities();
    }

    @Operation(summary = "Récuperer tous les activités d'une organisation")
    @GetMapping("organizations/{organizationId}")
    public List<Activity> getAllActivitiesByOrganizationId(@PathVariable String organizationId) {
        return activityService.getAllActivitiesByOrganizationId(organizationId);
    }


    @Operation(summary = "Récuperer une activité par son ID")
    @GetMapping("{activityId}")
    public ResponseEntity<Activity> getActivityIdById(@PathVariable String activityId) throws InterruptedException {
        return activityService.getActivityById(activityId);
    }

    @Operation(summary = "Modifier les données d'une activitée")
    @PatchMapping(value = "{activityId}")
    public ResponseEntity<Activity> patchActivityInfo(@PathVariable String activityId, @RequestBody Map<String, Object> activityPatchInfo) throws ExecutionException, InterruptedException {
        return activityService.patchActivityInfo(activityId, activityPatchInfo);
    }

}
