package com.infinity.serviceactivity.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.infinity.serviceactivity.exceptions.InternalServerException;
import com.infinity.serviceactivity.exceptions.NotFoundException;
import com.infinity.serviceactivity.feign.OrganizationServiceRestClient;
import com.infinity.serviceactivity.models.Assignment;
import com.infinity.serviceactivity.models.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AssignmentService {
    public static final String COLLECTION_NAME = "assignments";

    @Autowired
    private OrganizationServiceRestClient organizationServiceRestClient;

    public List<Assignment> getAllAssignments() {
        Firestore db = FirestoreClient.getFirestore();

        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).orderBy("creationDate", Query.Direction.DESCENDING).get();
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
    public ResponseEntity<Assignment> patchAssignmentInfo(String assignmentId, Map<String, Object> assignmentPatchInfo) {
        // Mise à jour des informations d'une mission
        try {
            ResponseEntity<Assignment> responseEntity = getAssignmentById(assignmentId);
            if(responseEntity.getStatusCode() == HttpStatus.OK) {
                Assignment assignment = responseEntity.getBody();
                if (assignment != null){
                    assignment.setTitle((String) assignmentPatchInfo.getOrDefault("title", assignment.getTitle()));
                    assignment.setDescription((String) assignmentPatchInfo.getOrDefault("description", assignment.getDescription()));
                    assignment.setDescriptionNeeds((String) assignmentPatchInfo.getOrDefault("descriptionNeeds", assignment.getDescriptionNeeds()));
                    assignment.setDescriptionResources((String) assignmentPatchInfo.getOrDefault("descriptionResources", assignment.getDescriptionResources()));

                    // Enregistrement de la modification dans la base de données
                    return updateAssignment(assignment, assignmentId);
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
            ResponseEntity<Organization> responseEntity = organizationServiceRestClient.getOrganizationById(organizationId);
            if(responseEntity.getStatusCode() == HttpStatus.OK){
                ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                        .orderBy("creationDate", Query.Direction.DESCENDING)
                        .whereEqualTo("organizationId", organizationId).get();

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
}
