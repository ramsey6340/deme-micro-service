package com.infinity.serviceorganization.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import com.infinity.serviceorganization.exceptions.InternalServerException;
import com.infinity.serviceorganization.exceptions.NotFoundException;
import com.infinity.serviceorganization.models.Organization;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class OrganizationService {

    public static final String COLLECTION_NAME = "organizations";


    public List<Organization> getAllOrganizations() {
        // Récuperer la liste des organizations
        Firestore db = FirestoreClient.getFirestore();
        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            return documents.stream().map(document -> document.toObject(Organization.class)).toList();

        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<Organization> getOrganizationById(String organisationId) throws InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        try {
            DocumentReference docRef = db.collection(COLLECTION_NAME).document(organisationId);
            ApiFuture<DocumentSnapshot> future = docRef.get();

            DocumentSnapshot document = future.get();
            if (document.exists()) {
                Organization user = document.toObject(Organization.class);
                return ResponseEntity.ok(user);
            }
            else {
                throw new NotFoundException("Organisation non trouvé");
            }
        }catch (Exception e){
            throw new InterruptedException(e.getMessage());
        }
    }

    public ResponseEntity<Organization> patchOrganizationInfo(String organisationId, Map<String, Object> userPatchInfo) {
        // Mise à jour des informations de l'organisation
        try {
            ResponseEntity<Organization> responseEntity = getOrganizationById(organisationId);
            if(responseEntity.getStatusCode() == HttpStatus.OK) {
                Organization organization = responseEntity.getBody();
                if (organization != null){
                    organization.setNumTel((String) userPatchInfo.getOrDefault("numTel", organization.getNumTel()));
                    organization.setActivated((boolean) userPatchInfo.getOrDefault("isActivated", organization.isActivated()));
                    organization.setName((String) userPatchInfo.getOrDefault("name", organization.getName()));
                    organization.setEmail((String) userPatchInfo.getOrDefault("email", organization.getEmail()));
                    organization.setLogin((String) userPatchInfo.getOrDefault("login", organization.getLogin()));
                    organization.setStartDateExercise((String) userPatchInfo.getOrDefault("startDateExercise", organization.getStartDateExercise()));
                    organization.setImageUrl((String) userPatchInfo.getOrDefault("imageUrl", organization.getImageUrl()));
                    organization.setDeviceType((String) userPatchInfo.getOrDefault("deviceType", organization.getDeviceType()));
                    organization.setAnonymous((boolean) userPatchInfo.getOrDefault("isAnonymous", organization.isAnonymous()));
                    organization.setDelete((boolean) userPatchInfo.getOrDefault("delete", organization.isDelete()));
                    organization.setProfile((String) userPatchInfo.getOrDefault("profile", organization.getProfile()));
                    organization.setMatricule((String) userPatchInfo.getOrDefault("matricule", organization.getMatricule()));
                    organization.setNbSubscription((int) userPatchInfo.getOrDefault("nbSubscription", organization.getNbSubscription()));
                    organization.setValid((boolean) userPatchInfo.getOrDefault("valid", organization.isValid()));
                    organization.setVerified((boolean) userPatchInfo.getOrDefault("verified", organization.isVerified()));
                    organization.setType((String) userPatchInfo.getOrDefault("type", organization.getType()));
                    organization.setSubscribersId((List<String>) userPatchInfo.getOrDefault("subscribersId", organization.getSubscribersId()));
                    organization.setPreferredPaymentMethods((List<String>) userPatchInfo.getOrDefault("preferredPaymentMethods", organization.getPreferredPaymentMethods()));
                    organization.setFavoriteHumanitarianCauses((List<String>) userPatchInfo.getOrDefault("favoriteHumanitarianCauses", organization.getFavoriteHumanitarianCauses()));

                    // Enregistrement de la modification dans la base de données
                    return updateOrganisation(organization, organisationId);
                }
            }
            throw new NotFoundException("Organisation non trouvé");
        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<Organization> updateOrganisation(Organization organization, String organisationId) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            ResponseEntity<Organization> responseEntity = getOrganizationById(organisationId);
            if (responseEntity.getStatusCode() == HttpStatus.OK){
                Organization organizationExist = responseEntity.getBody();
                if(organizationExist != null){
                    db.collection(COLLECTION_NAME).document(organization.getOrganizationId()).set(organization);
                    return ResponseEntity.ok(organization);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<Boolean> isLoginAvailable(String login) {
        // Verifier si le login est déjà utilisé
        Firestore db = FirestoreClient.getFirestore();
        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).whereEqualTo("login", login).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            if (documents.size() > 0) {
                return ResponseEntity.ok(false);
            } else {
                return new ResponseEntity<>(true, HttpStatus.NOT_FOUND);
            }
        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<String> resetPassword(String userId, String newPassword) {
        try{
            UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(userId)
                    .setPassword(newPassword);

            UserRecord userRecord = FirebaseAuth.getInstance().updateUser(request);
            return ResponseEntity.ok(userRecord.getUid());
        } catch (FirebaseAuthException e) {
            throw new InternalServerException(e.getMessage());
        }
    }
}
