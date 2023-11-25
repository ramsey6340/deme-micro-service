package com.infinity.serviceadmin.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.infinity.serviceadmin.exceptions.NotFoundException;
import com.infinity.serviceadmin.models.Admin;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class AdminService {
    public static final String COLLECTION_NAME = "admins";

    public ResponseEntity<Admin> getAdminById(String adminId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        try {
            DocumentReference docRef = db.collection(COLLECTION_NAME).document(adminId);
            ApiFuture<DocumentSnapshot> future = docRef.get();

            DocumentSnapshot document = future.get();
            if (document.exists()) {
                Admin user = document.toObject(Admin.class);
                return ResponseEntity.ok(user);
            }
            else {
                throw new NotFoundException("Admin non trouvé");
            }
        }catch (Exception e){
            throw new InterruptedException(e.getMessage());
        }
    }

}
