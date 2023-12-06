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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Service
public class AssignmentService {
    public static final String COLLECTION_NAME = "assignments";
    public static final String ORGANIZATION_COLLECTION_NAME = "organizations";
    public static final String CAUSE_COLLECTION_NAME = "causes";


    public ResponseEntity<String> createAssignment(String organizationId, String causeId, Assignment assignment) {
        Firestore db = FirestoreClient.getFirestore();
        try{
            DocumentReference docRefOrganization = db.collection(ORGANIZATION_COLLECTION_NAME).document(organizationId);
            if(docRefOrganization.get().get().exists()){
                DocumentReference docRefCause = db.collection(CAUSE_COLLECTION_NAME).document(causeId);
                if(docRefCause.get().get().exists()){
                    assignment.setOrganizationId(organizationId);
                    assignment.setCauseId(causeId);

                    ApiFuture<DocumentReference> docRef = db.collection(COLLECTION_NAME).add(assignment);
                    assignment.setAssignmentId(docRef.get().getId());
                    docRef.get().update("assignmentId", assignment.getAssignmentId());
                    docRef.get().update("cause", docRefCause);
                    docRef.get().update("organization", docRefOrganization);

                    URI location = ServletUriComponentsBuilder.
                            fromCurrentContextPath().path("{assignmentId}").
                            buildAndExpand(assignment.getAssignmentId()).toUri();

                    return ResponseEntity.created(location).body(assignment.getAssignmentId());
                }
                else{
                    throw new NotFoundException("Cause non trouvé");
                }
            }else {
                throw new NotFoundException("Organisation non trouvé");
            }
        }catch (Exception e){
            throw new InternalServerException(e.getMessage());
        }

    }

    public List<Assignment> getAllAssignments() {
        Firestore db = FirestoreClient.getFirestore();

        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("deleted", false)
                    .orderBy("creationDate", Query.Direction.DESCENDING).get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            return documents.stream().map(document -> document.toObject(Assignment.class)).toList();

        }catch (Exception e){
            throw new InternalServerException(e.getMessage());
        }
    }
    public ResponseEntity<Assignment> getAssignmentById(String assignmentId) throws InterruptedException {
        // Récupérer une mission par son ID
        Firestore db = FirestoreClient.getFirestore();
        try {
            DocumentReference docRef = db.collection(COLLECTION_NAME).document(assignmentId);
            ApiFuture<DocumentSnapshot> future = docRef.get();

            DocumentSnapshot document = future.get();
            if (document.exists()) {
                Assignment assignment = document.toObject(Assignment.class);
                return ResponseEntity.ok(assignment);
            }
            else {
                throw new NotFoundException("Mission non trouvé");
            }
        }catch (Exception e){
            throw new InterruptedException(e.getMessage());
        }
    }
    public ResponseEntity<Assignment> patchAssignmentInfo(String organizationId, String assignmentId, Map<String, Object> assignmentPatchInfo) {
        // Mise à jour des informations d'une mission
        try {
            ResponseEntity<Assignment> responseEntity = getAssignmentById(assignmentId);
            if(responseEntity.getStatusCode() == HttpStatus.OK) {
                Assignment assignment = responseEntity.getBody();

                if (assignment != null){
                    if(assignment.getOrganizationId().equals(organizationId)){
                        assignment.setTitle((String) assignmentPatchInfo.getOrDefault("title", assignment.getTitle()));
                        assignment.setDescription((String) assignmentPatchInfo.getOrDefault("description", assignment.getDescription()));
                        assignment.setDescriptionNeeds((String) assignmentPatchInfo.getOrDefault("descriptionNeeds", assignment.getDescriptionNeeds()));
                        assignment.setDescriptionResources((String) assignmentPatchInfo.getOrDefault("descriptionResources", assignment.getDescriptionResources()));

                        // Enregistrement de la modification dans la base de données
                        return updateAssignment(assignment, assignmentId);
                    }
                    else{
                        throw new BadRequestException("Cette organisation n'est pas autorisé à modifier cette mission");
                    }
                }
            }
            throw new NotFoundException("Mission non trouvé");
        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<Assignment> updateAssignment(Assignment assignment, String assignmentId) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            ResponseEntity<Assignment> responseEntity = getAssignmentById(assignmentId);
            if (responseEntity.getStatusCode() == HttpStatus.OK){
                Assignment assignmentExist = responseEntity.getBody();
                if(assignmentExist != null){
                    db.collection(COLLECTION_NAME).document(assignment.getAssignmentId()).set(assignment);
                    return ResponseEntity.ok(assignment);
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

    public List<Assignment> getAllAssignmentsByOrganizationId(String organizationId) {
        Firestore db = FirestoreClient.getFirestore();

        try {
            DocumentReference docRefOrganization = db.collection(ORGANIZATION_COLLECTION_NAME).document(organizationId);

            if(docRefOrganization.get().get().exists()){
                ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                        .whereEqualTo("deleted", false)
                        .whereEqualTo("organizationId", organizationId)
                        .orderBy("creationDate", Query.Direction.DESCENDING).get();

                List<QueryDocumentSnapshot> documents = future.get().getDocuments();
                return documents.stream().map(document -> document.toObject(Assignment.class)).toList();
            }
            else {
                throw new NotFoundException("Organisation non trouvé");
            }

        }catch (Exception e){
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<String> deleteAssignment(String organizationId, String assignmentId) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            DocumentReference docRefAssignment = db.collection(COLLECTION_NAME).document(assignmentId);
            if (docRefAssignment.get().get().exists()) {
                Assignment assignment = docRefAssignment.get().get().toObject(Assignment.class);
                if(assignment.getOrganizationId().equals(organizationId)) {
                    docRefAssignment.update("deleted", true);
                    return ResponseEntity.ok(assignmentId);
                }
                else{
                    throw new BadRequestException("Cette organisation n'est pas autorisé à supprimer cette mission");
                }
            }
            else {
                throw new NotFoundException("Mission non trouvé");
            }
        }catch (Exception e){
            throw new InternalServerException(e.getMessage());
        }
    }
}
