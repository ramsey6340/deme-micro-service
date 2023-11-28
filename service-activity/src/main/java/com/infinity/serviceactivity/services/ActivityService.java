package com.infinity.serviceactivity.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.infinity.serviceactivity.exceptions.BadRequestException;
import com.infinity.serviceactivity.exceptions.InternalServerException;
import com.infinity.serviceactivity.exceptions.NotFoundException;
import com.infinity.serviceactivity.feign.OrganizationServiceRestClient;
import com.infinity.serviceactivity.models.Activity;
import com.infinity.serviceactivity.models.Assignment;
import com.infinity.serviceactivity.models.Organization;
import com.infinity.serviceactivity.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Service
public class ActivityService {
    private static final String COLLECTION_NAME = "activities";
    private static final String ASSIGNMENT_COLLECTION_NAME = "assignments";
    private static final String ORGANIZATION_COLLECTION_NAME = "organizations";

    @Autowired
    private AssignmentService assignmentService;

    public ResponseEntity<String> createActivity(String organizationId, String assigmentId, Activity activity) {
        Firestore db = FirestoreClient.getFirestore();
        try{
            // Vérification si la mission existe
            DocumentReference docRefAssigment = db.collection(ASSIGNMENT_COLLECTION_NAME).document(assigmentId);
            if(docRefAssigment.get().get().exists()){
                Assignment assignment = docRefAssigment.get().get().toObject(Assignment.class);
                if(assignment.getOrganizationId().equals(organizationId)){
                    ApiFuture<DocumentReference> docRef = db.collection(COLLECTION_NAME).add(activity);
                    activity.setActivityId(docRef.get().getId());
                    docRef.get().update("activityId", activity.getActivityId());

                    URI location = ServletUriComponentsBuilder.
                            fromCurrentContextPath().path("{activityId}").
                            buildAndExpand(activity.getActivityId()).toUri();

                    return ResponseEntity.created(location).body(activity.getActivityId());

                }
                else{
                    throw new BadRequestException("Cette organisation n'est pas autorisé à créer une activité avec cette mission");
                }
            }else {
                throw new NotFoundException("Mission non trouvé");
            }
        }catch (Exception e){
            throw new InternalServerException(e.getMessage());
        }

    }

    public List<Activity> getAllActivities() {
        Firestore db = FirestoreClient.getFirestore();

        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("deleted", false)
                    .orderBy("creationDate", Query.Direction.DESCENDING).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            return documents.stream().map(document -> document.toObject(Activity.class)).toList();

        }catch (Exception e){
            throw new InternalServerException(e.getMessage());
        }
    }
    public ResponseEntity<Activity> getActivityById(String activityId) throws InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        try {
            DocumentReference docRef = db.collection(COLLECTION_NAME).document(activityId);
            ApiFuture<DocumentSnapshot> future = docRef.get();

            DocumentSnapshot document = future.get();
            if (document.exists()) {
                Activity activity = document.toObject(Activity.class);
                return ResponseEntity.ok(activity);
            }
            else {
                throw new NotFoundException("Activité non trouvé");
            }
        }catch (Exception e){
            throw new InterruptedException(e.getMessage());
        }
    }
    public ResponseEntity<Activity> patchActivityInfo(String organizationId, String activityId, Map<String, Object> activityPatchInfo) {
        Firestore db = FirestoreClient.getFirestore();
        // Mise à jour des informations d'une activité
        try {
            ResponseEntity<Activity> responseEntity = getActivityById(activityId);
            if(responseEntity.getStatusCode() == HttpStatus.OK) {
                Activity activity = responseEntity.getBody();
                DocumentReference docRefAssigment = db.collection(ASSIGNMENT_COLLECTION_NAME).document(activity.getAssignmentId());
                if(docRefAssigment.get().get().exists()){
                    Assignment assignment = docRefAssigment.get().get().toObject(Assignment.class);
                    if(assignment.getOrganizationId().equals(organizationId)){
                        activity.setTitle((String) activityPatchInfo.getOrDefault("title", activity.getTitle()));
                        activity.setDescription((String) activityPatchInfo.getOrDefault("description", activity.getDescription()));
                        activity.setStartDate((String) activityPatchInfo.getOrDefault("startDate", activity.getStartDate()));
                        activity.setEndDate((String) activityPatchInfo.getOrDefault("endDate", activity.getEndDate()));

                        // Enregistrement de la modification dans la base de données
                        return updateActivity(activity, activityId);
                    }
                    else{
                        throw new BadRequestException("Cette organisation n'est pas autorisé à modifier cette activité");
                    }
                }
                else {
                    throw new NotFoundException("Mission non trouvé");
                }
            }
            throw new NotFoundException("Activité non trouvé");
        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<Activity> updateActivity(Activity activity, String activityId) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            ResponseEntity<Activity> responseEntity = getActivityById(activityId);
            if (responseEntity.getStatusCode() == HttpStatus.OK){
                Activity activityExist = responseEntity.getBody();
                if(activityExist != null){
                    db.collection(COLLECTION_NAME).document(activity.getActivityId()).set(activity);
                    return ResponseEntity.ok(activity);
                }
            }
            else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public List<Activity> getAllActivitiesByOrganizationId(String organizationId) {
        Firestore db = FirestoreClient.getFirestore();

        try {
            DocumentReference docRefOrganization = db.collection(ORGANIZATION_COLLECTION_NAME).document(organizationId);
            if(docRefOrganization.get().get().exists()){
                List<Assignment> assignments = assignmentService.getAllAssignmentsByOrganizationId(organizationId);

                ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                        .whereEqualTo("deleted", false)
                        .whereIn("assignmentId", assignments)
                        .orderBy("creationDate", Query.Direction.DESCENDING)
                        .get();

                List<QueryDocumentSnapshot> documents = future.get().getDocuments();
                return documents.stream().map(document -> document.toObject(Activity.class)).toList();
            }
            else {
                throw new NotFoundException("Organisation non trouvé");
            }

        }catch (Exception e){
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<String> deleteActivity(String organizationId, String activityId) {
        Firestore db = FirestoreClient.getFirestore();

        try {
            DocumentReference docRefActivity = db.collection(COLLECTION_NAME).document(activityId);

            if(docRefActivity.get().get().exists()) {
                Activity activity = docRefActivity.get().get().toObject(Activity.class);
                DocumentReference docRefAssignment = db.collection(ASSIGNMENT_COLLECTION_NAME).document(activity.getAssignmentId());
                if(docRefAssignment.get().get().exists()) {
                    Assignment assignment = docRefAssignment.get().get().toObject(Assignment.class);
                    DocumentReference docRefOrganization = db.collection(ORGANIZATION_COLLECTION_NAME).document(assignment.getOrganizationId());
                    if(docRefOrganization.get().get().exists()) {
                        Organization organization = docRefOrganization.get().get().toObject(Organization.class);
                        if(organization.getOrganizationId().equals(organizationId)){
                            docRefActivity.update("deleted", true);
                            return ResponseEntity.ok(activityId);
                        }
                        else {
                            throw new BadRequestException("Cet organisation n'est pas autorisé à supprimer cette activité");
                        }
                    }
                    else {
                        throw new NotFoundException("Cet organisation n'existe pas");
                    }
                }
                else{
                    throw new NotFoundException("La mission correspondant à cette activité n'existe pas");
                }
            }
            else{
                throw new BadRequestException("Cette activité n'existe pas");
            }
        }catch (Exception e){
            throw new InternalServerException(e.getMessage());
        }
    }
}
