package com.infinity.servicedonation.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.infinity.servicedonation.exceptions.BadRequestException;
import com.infinity.servicedonation.exceptions.InternalServerException;
import com.infinity.servicedonation.exceptions.NotFoundException;
import com.infinity.servicedonation.models.Assignment;
import com.infinity.servicedonation.models.Demand;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DemandService {

    public static final String COLLECTION_NAME = "demands";
    public static final String ORGANIZATION_COLLECTION_NAME = "organizations";
    public static final String USER_COLLECTION_NAME = "users";
    public static final String CAUSE_COLLECTION_NAME = "causes";
    public static final String ASSIGNMENT_COLLECTION_NAME = "assignments";
    public List<Demand> getAllDemands() {
        Firestore db = FirestoreClient.getFirestore();

        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("deleted", false)
                    .orderBy("creationDate", Query.Direction.DESCENDING).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            return documents.stream().map(document -> document.toObject(Demand.class)).toList();

        }catch (Exception e){
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<Demand> getDemandById(String demandId) throws InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        try {
            DocumentReference docRef = db.collection(COLLECTION_NAME).document(demandId);
            ApiFuture<DocumentSnapshot> future = docRef.get();

            DocumentSnapshot document = future.get();
            if (document.exists()) {
                Demand demand = document.toObject(Demand.class);
                return ResponseEntity.ok(demand);
            }
            else {
                throw new NotFoundException("Demande non trouvé");
            }
        }catch (Exception e){
            throw new InterruptedException(e.getMessage());
        }
    }

    public ResponseEntity<Demand> patchDemandInfoByOrganization(String organizationId, String demandId, Map<String, Object> demandPatchInfo) {
        // Mise à jour des informations d'une demande
        try {
            ResponseEntity<Demand> responseEntity = getDemandById(demandId);
            if(responseEntity.getStatusCode() == HttpStatus.OK) {
                Demand demand = responseEntity.getBody();
                if (demand != null){
                    if (demand.getOrganizationId().equals(organizationId)){
                        // Enregistrement de la modification dans la base de données
                        return updateDemand(demandPatchInfo, demandId);
                    }
                    else{
                        throw new BadRequestException("Cette organisation n'est pas autorisé pas modifier cette demande");
                    }
                }
            }
            throw new NotFoundException("Demande non trouvé");
        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }
    public ResponseEntity<Demand> patchDemandInfoByUser(String userId, String demandId, Map<String, Object> demandPatchInfo) {
        // Mise à jour des informations d'une demande
        try {
            ResponseEntity<Demand> responseEntity = getDemandById(demandId);
            if(responseEntity.getStatusCode() == HttpStatus.OK) {
                Demand demand = responseEntity.getBody();
                if (demand != null){
                    if(demand.getUserId().equals(userId)){
                        // Enregistrement de la modification dans la base de données
                        return updateDemand(demandPatchInfo, demandId);
                    }
                    else {
                        throw new BadRequestException("Ce user n'est pas autorisé pas modifier cette demande");
                    }
                }
            }
            throw new NotFoundException("Demande non trouvé");
        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<Demand> updateDemand(Map<String, Object> demandPatchInfo, String demandId) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            ResponseEntity<Demand> responseEntity = getDemandById(demandId);
            if (responseEntity.getStatusCode() == HttpStatus.OK){
                Demand demand = responseEntity.getBody();
                if(demand != null){
                    demand.setDescription((String) demandPatchInfo.getOrDefault("description", demand.getDescription()));
                    demand.setVideoUrl((String) demandPatchInfo.getOrDefault("videoUrl", demand.getVideoUrl()));
                    demand.setImageUrl((String) demandPatchInfo.getOrDefault("imageUrl", demand.getImageUrl()));
                    demand.setCauseId((String) demandPatchInfo.getOrDefault("CauseId", demand.getCauseId()));
                    demand.setActive((boolean) demandPatchInfo.getOrDefault("active", demand.isActive()));
                    demand.setGuarantorId((String) demandPatchInfo.getOrDefault("guarantorId", demand.getGuarantorId()));


                    db.collection(COLLECTION_NAME).document(demand.getDemandId()).set(demand);
                    return ResponseEntity.ok(demand);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<String> createDemandByOrganization(String organizationId,
                                                             String assignmentId,
                                                             Demand demand) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            DocumentReference docRefOrganization = db.collection(ORGANIZATION_COLLECTION_NAME).document(organizationId);
            if(docRefOrganization.get().get().exists()){
                DocumentReference docRefAssignment = db.collection(ASSIGNMENT_COLLECTION_NAME).document(assignmentId);
                if(docRefAssignment.get().get().exists()){
                    Assignment  assignment = docRefAssignment.get().get().toObject(Assignment.class);

                    // Récuperation des réferences
                    DocumentReference docRefCause = db.collection(CAUSE_COLLECTION_NAME).document(assignment.getCauseId());


                    demand.setGuarantorId(organizationId);
                    demand.setCauseId(assignment.getCauseId());
                    demand.setUserId(null);
                    demand.setOrganizationId(organizationId);
                    List<DocumentReference> docRefList = new ArrayList<>();
                    docRefList.add(docRefCause);
                    docRefList.add(docRefOrganization);
                    docRefList.add(docRefOrganization);
                    docRefList.add(null);

                    return createDemand(demand, docRefList);
                }
                else{
                    throw new NotFoundException("Mission non trouvé");
                }
            }
            else{
                throw new NotFoundException("Organisation non trouvé");
            }
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<String> createDemand(Demand demand, List<DocumentReference> docRefList) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            if((demand.getDescription() != null && !demand.getDescription().isEmpty()) | (demand.getVideoUrl()!=null && !demand.getVideoUrl().isEmpty())) {
                ApiFuture<DocumentReference> docRef = db.collection(COLLECTION_NAME).add(demand);
                demand.setDemandId(docRef.get().getId());

                DocumentReference updateDocRef = db.collection(COLLECTION_NAME).document(demand.getDemandId());
                updateDocRef.update("demandId", demand.getDemandId());
                updateDocRef.update("cause", docRefList.get(0));
                updateDocRef.update("organization", docRefList.get(1));
                updateDocRef.update("guarantor", docRefList.get(2));
                updateDocRef.update("user", docRefList.get(3));

                URI location = ServletUriComponentsBuilder.
                        fromCurrentContextPath().path("{demandId}").
                        buildAndExpand(demand.getDemandId()).toUri();

                return ResponseEntity.created(location).body(demand.getDemandId());
            }
            throw new BadRequestException("Le temoignage doit contenir un message ou une video");
        }catch (Exception e){
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<String> createDemandByUser(String userId,
                                                     String causeId,
                                                     String guarantorId,
                                                     Demand demand) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            DocumentReference docRefUser = db.collection(USER_COLLECTION_NAME).document(userId);
            if(docRefUser.get().get().exists()){
                DocumentReference docRefCause = db.collection(CAUSE_COLLECTION_NAME).document(causeId);
                if(docRefCause.get().get().exists()){
                    DocumentReference docRefOrganization = db.collection(ORGANIZATION_COLLECTION_NAME).document(guarantorId);
                    if(docRefOrganization.get().get().exists()){
                        demand.setGuarantorId(guarantorId);
                        demand.setCauseId(causeId);
                        demand.setUserId(userId);
                        demand.setOrganizationId(null);

                        List<DocumentReference> docRefList = new ArrayList<>();
                        docRefList.add(docRefCause);
                        docRefList.add(docRefOrganization);
                        docRefList.add(null);
                        docRefList.add(docRefUser);

                        return createDemand(demand, docRefList);
                    }
                    else{
                        throw new NotFoundException("Organisation non trouvé");
                    }
                }
                else{
                    throw new NotFoundException("Cause non trouvé");
                }
            }
            else{
                throw new NotFoundException("User non trouvé");
            }
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<String> deleteDemandByOrganization(String organizationId, String demandId) throws InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        try {
            DocumentReference docRefOrganization = db.collection(ORGANIZATION_COLLECTION_NAME).document(organizationId);

            if(docRefOrganization.get().get().exists()) {
                ResponseEntity<Demand> responseEntityDemand  = getDemandById(demandId);
                if(responseEntityDemand.getStatusCode() == HttpStatus.OK){
                    Demand demand = responseEntityDemand.getBody();
                    if(demand.getOrganizationId() != null) {
                        if(demand.getOrganizationId().equals(organizationId)) {
                            DocumentReference docRefDemand = db.collection(COLLECTION_NAME).document(demandId);
                            docRefDemand.update("deleted", true);
                            return ResponseEntity.ok(demandId);
                        }
                        else{
                            throw new BadRequestException("Vous n'ête pas autorisé à supprimer cette demande");
                        }
                    }
                    else{
                        throw new NotFoundException("Cet organisation n'a pas créer cette demande");
                    }
                }
                else{
                    throw new NotFoundException("Cette demande n'existe pas");
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
    public ResponseEntity<String> deleteDemandByUser(String userId, String demandId) throws InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        try {
            DocumentReference docRefUser = db.collection(USER_COLLECTION_NAME).document(userId);

            if(docRefUser.get().get().exists()) {
                ResponseEntity<Demand> responseEntityDemand  = getDemandById(demandId);
                if(responseEntityDemand.getStatusCode() == HttpStatus.OK){
                    Demand demand = responseEntityDemand.getBody();
                    if(demand.getUserId() != null) {
                        if(demand.getUserId().equals(userId)) {
                            DocumentReference docRefDemand = db.collection(COLLECTION_NAME).document(demandId);
                            docRefDemand.update("deleted", true);
                            return ResponseEntity.ok(demandId);
                        }
                        else{
                            throw new BadRequestException("Vous n'ête pas autorisé à supprimer cette demande");
                        }
                    }
                    else{
                        throw new NotFoundException("Cette demande n'a pas été créée par ce user");
                    }
                }
                else{
                    throw new NotFoundException("Cette demande n'existe pas");
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


    public List<Demand> getAllDemandsForUser(String userId) {
        Firestore db = FirestoreClient.getFirestore();

        try {
            DocumentReference docRefUser = db.collection(USER_COLLECTION_NAME).document(userId);

            if(docRefUser.get().get().exists()){
                ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                        .whereEqualTo("deleted", false)
                        .whereEqualTo("userId", userId)
                        .orderBy("creationDate", Query.Direction.DESCENDING).get();

                List<QueryDocumentSnapshot> documents = future.get().getDocuments();
                return documents.stream().map(document -> document.toObject(Demand.class)).toList();
            }
            else {
                throw new NotFoundException("User non trouvé");
            }

        }catch (Exception e){
            throw new InternalServerException(e.getMessage());
        }
    }
    public List<Demand> getAllDemandsForOrganization(String organizationId) {
        Firestore db = FirestoreClient.getFirestore();

        try {
            DocumentReference docRefUser = db.collection(ORGANIZATION_COLLECTION_NAME).document(organizationId);

            if(docRefUser.get().get().exists()){
                ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                        .whereEqualTo("deleted", false)
                        .whereEqualTo("organizationId", organizationId)
                        .orderBy("creationDate", Query.Direction.DESCENDING).get();

                List<QueryDocumentSnapshot> documents = future.get().getDocuments();
                return documents.stream().map(document -> document.toObject(Demand.class)).toList();
            }
            else {
                throw new NotFoundException("Organisation non trouvé");
            }

        }catch (Exception e){
            throw new InternalServerException(e.getMessage());
        }
    }
}
