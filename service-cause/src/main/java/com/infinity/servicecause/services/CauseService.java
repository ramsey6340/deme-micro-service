package com.infinity.servicecause.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.infinity.servicecause.exceptions.InternalServerException;
import com.infinity.servicecause.exceptions.NotFoundException;
import com.infinity.servicecause.feign.AdminServiceRestClient;
import com.infinity.servicecause.models.Admin;
import com.infinity.servicecause.models.Cause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
public class CauseService {
    public static final String COLLECTION_NAME = "causes";

    @Autowired
    private AdminServiceRestClient adminServiceRestClient;

    public ResponseEntity<Cause> createCause(String adminId, Cause cause) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            ResponseEntity<Admin> responseEntity = adminServiceRestClient.getAdminById(adminId);
            if(responseEntity.getStatusCode() == HttpStatus.OK) {
                cause.setAdminId(adminId);
                ApiFuture<DocumentReference> docRef = db.collection(COLLECTION_NAME).add(cause);
                cause.setCauseId(docRef.get().getId());

                docRef.get().update("causeId", docRef.get().getId());

                URI location = ServletUriComponentsBuilder.
                        fromCurrentContextPath().path("{causeId}").
                        buildAndExpand(cause.getCauseId()).toUri();

                return ResponseEntity.created(location).body(cause);
            }
            throw new NotFoundException("Cet administrateur n'existe pas");
        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public List<Cause> getAllCauses(){
        Firestore db = FirestoreClient.getFirestore();

        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("deleted", false).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            return documents.stream().map(document -> document.toObject(Cause.class)).toList();

        }catch (Exception e){
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<Cause> getCauseById(String causeId) throws InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        try {
            DocumentReference docRef = db.collection(COLLECTION_NAME).document(causeId);
            ApiFuture<DocumentSnapshot> future = docRef.get();

            DocumentSnapshot document = future.get();
            if (document.exists()) {
                Cause cause = document.toObject(Cause.class);
                return ResponseEntity.ok(cause);
            }
            else {
                throw new NotFoundException("Cause non trouvé");
            }
        }catch (Exception e){
            throw new InterruptedException(e.getMessage());
        }
    }
}
