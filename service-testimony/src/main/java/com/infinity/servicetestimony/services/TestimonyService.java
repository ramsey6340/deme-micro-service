package com.infinity.servicetestimony.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import com.infinity.servicetestimony.exceptions.BadRequestException;
import com.infinity.servicetestimony.exceptions.InternalServerException;
import com.infinity.servicetestimony.exceptions.NotFoundException;
import com.infinity.servicetestimony.exceptions.RessourceExistantException;
import com.infinity.servicetestimony.feign.CauseServiceRestClient;
import com.infinity.servicetestimony.feign.OrganizationServiceRestClient;
import com.infinity.servicetestimony.feign.UserServiceRestClient;
import com.infinity.servicetestimony.models.Cause;
import com.infinity.servicetestimony.models.Organization;
import com.infinity.servicetestimony.models.Testimony;
import com.infinity.servicetestimony.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class TestimonyService {
    public static final String COLLECTION_NAME = "testimonies";

    @Autowired
    private OrganizationServiceRestClient organizationServiceRestClient;
    @Autowired
    private CauseServiceRestClient causeServiceRestClient;
    @Autowired
    private UserServiceRestClient userServiceRestClient;
    public List<Testimony> getAllTestimonies() {
        Firestore db = FirestoreClient.getFirestore();

        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("deleted", false)
                    .orderBy("creationDate", Query.Direction.DESCENDING).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            return documents.stream().map(document -> document.toObject(Testimony.class)).toList();

        }catch (Exception e){
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<Testimony> getTestimonyById(String postId) throws InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        try {
            DocumentReference docRef = db.collection(COLLECTION_NAME).document(postId);
            ApiFuture<DocumentSnapshot> future = docRef.get();

            DocumentSnapshot document = future.get();
            if (document.exists()) {
                Testimony testimony = document.toObject(Testimony.class);
                return ResponseEntity.ok(testimony);
            }
            else {
                throw new NotFoundException("Temoignage non trouvé");
            }
        }catch (Exception e){
            throw new InterruptedException(e.getMessage());
        }
    }

    public ResponseEntity<Testimony> patchTestimonyInfo(String testimonyId, Map<String, Object> testimonyPatchInfo) {
        // Mise à jour des informations d'un post
        try {
            ResponseEntity<Testimony> responseEntity = getTestimonyById(testimonyId);
            if(responseEntity.getStatusCode() == HttpStatus.OK) {
                Testimony testimony = responseEntity.getBody();
                if (testimony != null){
                    testimony.setMessage((String) testimonyPatchInfo.getOrDefault("message", testimony.getMessage()));
                    testimony.setVideoUrl((String) testimonyPatchInfo.getOrDefault("videoUrl", testimony.getVideoUrl()));
                    testimony.setImageUrl((String) testimonyPatchInfo.getOrDefault("imageUrl", testimony.getImageUrl()));


                    // Enregistrement de la modification dans la base de données
                    return updateTestimony(testimony, testimonyId);
                }
            }
            throw new NotFoundException("Post non trouvé");
        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<Testimony> updateTestimony(Testimony testimony, String testimonyId) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            ResponseEntity<Testimony> responseEntity = getTestimonyById(testimonyId);
            if (responseEntity.getStatusCode() == HttpStatus.OK){
                Testimony testimonyExist = responseEntity.getBody();
                if(testimonyExist != null){
                    db.collection(COLLECTION_NAME).document(testimony.getTestimonyId()).set(testimony);
                    return ResponseEntity.ok(testimony);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<String> createTestimonyByOrganization(String organizationId, Testimony testimony) {
        Firestore db = FirestoreClient.getFirestore();
        try {
             ResponseEntity<Cause> responseEntityCause = causeServiceRestClient.getCauseById(testimony.getCauseId());
             if(responseEntityCause.getStatusCode() == HttpStatus.OK) {
                 ResponseEntity<Organization> responseEntityOrganization = organizationServiceRestClient.getOrganizationById(organizationId);
                 if (responseEntityOrganization.getStatusCode() == HttpStatus.OK && testimony.getUserId() == null){
                     return createTestimony(testimony);
                 }
                 throw new BadRequestException("Impossible de créer ce temoignage: problème au niveau de l'organisation ou de l'utilisateur");
             }
             throw new NotFoundException("La cause spécifier est incorrecte");

        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<String> createTestimony(Testimony testimony) {
        Firestore db = FirestoreClient.getFirestore();
        try {
           if((testimony.getMessage() != null && !testimony.getMessage().isEmpty()) | (testimony.getVideoUrl()!=null && !testimony.getVideoUrl().isEmpty())) {
               ApiFuture<DocumentReference> docRef = db.collection(COLLECTION_NAME).add(testimony);
               testimony.setTestimonyId(docRef.get().getId());

               DocumentReference updateDocRef = db.collection(COLLECTION_NAME).document(testimony.getTestimonyId());
               updateDocRef.update("testimonyId", testimony.getTestimonyId());

               URI location = ServletUriComponentsBuilder.
                       fromCurrentContextPath().path("{testimonyId}").
                       buildAndExpand(testimony.getTestimonyId()).toUri();

               return ResponseEntity.created(location).body(testimony.getTestimonyId());
           }
           throw new BadRequestException("Le temoignage doit contenir un message ou une video");
        }catch (Exception e){
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<String> createTestimonyByUser(String userId, Testimony testimony) {
        Firestore db = FirestoreClient.getFirestore();
        try {
             ResponseEntity<Cause> responseEntityCause = causeServiceRestClient.getCauseById(testimony.getCauseId());
             if(responseEntityCause.getStatusCode() == HttpStatus.OK) {
                 ResponseEntity<User> responseEntityUser = userServiceRestClient.getUserById(userId);
                 if (responseEntityUser.getStatusCode() == HttpStatus.OK && testimony.getOrganizationId() == null){
                     return createTestimony(testimony);
                 }
                 throw new BadRequestException("Impossible de créer ce temoignage: problème au niveau de l'organisation ou de l'utilisateur");
             }
             throw new NotFoundException("La cause spécifier est incorrecte");

        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<String> deleteTestimonyByOrganization(String organizationId, String testimonyId) throws InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        try {
            ResponseEntity<Organization> responseEntity = organizationServiceRestClient.getOrganizationById(organizationId);
            if(responseEntity.getStatusCode() == HttpStatus.OK) {
                ResponseEntity<Testimony> responseEntityTestimony  = getTestimonyById(testimonyId);
                if(responseEntityTestimony.getStatusCode() == HttpStatus.OK){
                    Testimony testimony = responseEntityTestimony.getBody();
                    if(testimony.getOrganizationId() != null) {
                        if(testimony.getOrganizationId().equals(organizationId)) {
                            ApiFuture<WriteResult> writeResult = db.collection(COLLECTION_NAME).document(testimonyId).delete();
                            return ResponseEntity.ok(testimonyId);
                        }
                        else{
                            throw new BadRequestException("Vous n'ête pas autorisé à supprimer ce temoignage");
                        }
                    }
                    else{
                        throw new NotFoundException("Cet organisation n'a pas créer ce temoignage");
                    }
                }
                else{
                    throw new NotFoundException("Cet temoignage n'existe pas");
                }
            }
            else{
                throw new NotFoundException("Cet organisation n'existe pas");
            }
        }
        catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }
    public ResponseEntity<String> deleteTestimonyByUser(String userId, String testimonyId) throws InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        try {
            ResponseEntity<User> responseEntity = userServiceRestClient.getUserById(userId);
            if(responseEntity.getStatusCode() == HttpStatus.OK) {
                ResponseEntity<Testimony> responseEntityTestimony  = getTestimonyById(testimonyId);
                if(responseEntityTestimony.getStatusCode() == HttpStatus.OK){
                    Testimony testimony = responseEntityTestimony.getBody();
                    if(testimony.getUserId() != null) {
                        if(testimony.getOrganizationId().equals(userId)) {
                            ApiFuture<WriteResult> writeResult = db.collection(COLLECTION_NAME).document(testimonyId).delete();
                            return ResponseEntity.ok(testimonyId);
                        }
                        else{
                            throw new BadRequestException("Vous n'ête pas autorisé à supprimer ce temoignage");
                        }
                    }
                    else{
                        throw new NotFoundException("Cet organisation n'a pas créer ce temoignage");
                    }
                }
                else{
                    throw new NotFoundException("Cet temoignage n'existe pas");
                }
            }
            else{
                throw new NotFoundException("Cet user n'existe pas");
            }
        }
        catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

}
