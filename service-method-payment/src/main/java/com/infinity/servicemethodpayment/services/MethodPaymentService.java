package com.infinity.servicemethodpayment.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.infinity.servicemethodpayment.exceptions.InternalServerException;
import com.infinity.servicemethodpayment.models.MethodPayment;
import jakarta.ws.rs.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MethodPaymentService {
    public static final String COLLECTION_NAME = "methodPayments";

    public List<MethodPayment> getAllMethodPayment(){
        Firestore db = FirestoreClient.getFirestore();

        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("deleted", false)
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            return documents.stream().map(document -> document.toObject(MethodPayment.class)).toList();

        }catch (Exception e){
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<MethodPayment> getMethodPaymentById(String methodPaymentId) {
        try {
            Firestore db = FirestoreClient.getFirestore();

            ApiFuture<DocumentSnapshot> documentSnapshot = db.collection(COLLECTION_NAME).document(methodPaymentId).get();
            if(documentSnapshot.get().exists()) {
                MethodPayment methodPayment = documentSnapshot.get().toObject(MethodPayment.class);
                return ResponseEntity.ok(methodPayment);
            }
            else {
                throw new NotFoundException("Methode de paiement non trouvé");
            }
        }catch (Exception e){
            throw new InternalServerException(e.getMessage());
        }
    }
}
