package com.infinity.servicedonation.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.infinity.servicedonation.exceptions.BadRequestException;
import com.infinity.servicedonation.exceptions.InternalServerException;
import com.infinity.servicedonation.exceptions.NotFoundException;
import com.infinity.servicedonation.feign.CauseServiceRestClient;
import com.infinity.servicedonation.feign.OrganizationServiceRestClient;
import com.infinity.servicedonation.feign.UserServiceRestClient;
import com.infinity.servicedonation.models.Cause;
import com.infinity.servicedonation.models.Demand;
import com.infinity.servicedonation.models.Organization;
import com.infinity.servicedonation.models.User;
import com.infinity.servicetestimony.models.Testimony;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Service
public class DemandService {

    public static final String COLLECTION_NAME = "demands";
    @Autowired
    private UserServiceRestClient userServiceRestClient;
    @Autowired
    private CauseServiceRestClient causeServiceRestClient;
    @Autowired
    private OrganizationServiceRestClient organizationServiceRestClient;

    public List<Demand> getAllDemands() {
        Firestore db = FirestoreClient.getFirestore();

        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).orderBy("creationDate", Query.Direction.DESCENDING).get();
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

    public ResponseEntity<Demand> patchDemandInfo(String demandId, Map<String, Object> demandPatchInfo) {
        // Mise à jour des informations d'une demande
        try {
            ResponseEntity<Demand> responseEntity = getDemandById(demandId);
            if(responseEntity.getStatusCode() == HttpStatus.OK) {
                Demand demand = responseEntity.getBody();
                if (demand != null){
                    demand.setDescription((String) demandPatchInfo.getOrDefault("description", demand.getDescription()));
                    demand.setVideoUrl((String) demandPatchInfo.getOrDefault("videoUrl", demand.getVideoUrl()));
                    demand.setImageUrl((String) demandPatchInfo.getOrDefault("imageUrl", demand.getImageUrl()));
                    demand.setCauseId((String) demandPatchInfo.getOrDefault("CauseId", demand.getCauseId()));
                    demand.setActive((boolean) demandPatchInfo.getOrDefault("active", demand.isActive()));
                    demand.setGuarantorId((String) demandPatchInfo.getOrDefault("guarantorId", demand.getGuarantorId()));

                    // Enregistrement de la modification dans la base de données
                    return updateDemand(demand, demandId);
                }
            }
            throw new NotFoundException("Demande non trouvé");
        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<Demand> updateDemand(Demand demand, String demandId) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            ResponseEntity<Demand> responseEntity = getDemandById(demandId);
            if (responseEntity.getStatusCode() == HttpStatus.OK){
                Demand demandExist = responseEntity.getBody();
                if(demandExist != null){
                    db.collection(COLLECTION_NAME).document(demand.getDemandId()).set(demand);
                    return ResponseEntity.ok(demand);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<String> createDemandByOrganization(String organizationId, Demand demand) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            ResponseEntity<Cause> responseEntityCause = causeServiceRestClient.getCauseById(demand.getCauseId());
            if(responseEntityCause.getStatusCode() == HttpStatus.OK) {
                ResponseEntity<Organization> responseEntityOrganization = organizationServiceRestClient.getOrganizationById(organizationId);
                if (responseEntityOrganization.getStatusCode() == HttpStatus.OK && demand.getUserId() == null){
                    return createDemand(demand);
                }
                throw new BadRequestException("Impossible de créer ce temoignage: problème au niveau de l'organisation ou de l'utilisateur");
            }
            throw new NotFoundException("La cause spécifier est incorrecte");

        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<String> createDemand(Demand demand) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            if((demand.getDescription() != null && !demand.getDescription().isEmpty()) | (demand.getVideoUrl()!=null && !demand.getVideoUrl().isEmpty())) {
                ApiFuture<DocumentReference> docRef = db.collection(COLLECTION_NAME).add(demand);
                demand.setDemandId(docRef.get().getId());

                DocumentReference updateDocRef = db.collection(COLLECTION_NAME).document(demand.getDemandId());
                updateDocRef.update("demandId", demand.getDemandId());

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

    public ResponseEntity<String> createDemandByUser(String userId, Demand demand) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            ResponseEntity<Cause> responseEntityCause = causeServiceRestClient.getCauseById(demand.getCauseId());
            if(responseEntityCause.getStatusCode() == HttpStatus.OK) {
                ResponseEntity<User> responseEntityUser = userServiceRestClient.getUserById(userId);
                if (responseEntityUser.getStatusCode() == HttpStatus.OK && demand.getOrganizationId() == null){
                    return createDemand(demand);
                }
                throw new BadRequestException("Impossible de créer cette demande: problème au niveau de l'organisation ou de l'utilisateur");
            }
            throw new NotFoundException("La cause spécifier est incorrecte");

        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<String> deleteDemandByOrganization(String organizationId, String testimonyId) throws InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        try {
            ResponseEntity<Organization> responseEntity = organizationServiceRestClient.getOrganizationById(organizationId);
            if(responseEntity.getStatusCode() == HttpStatus.OK) {
                ResponseEntity<Demand> responseEntityDemand  = getDemandById(testimonyId);
                if(responseEntityDemand.getStatusCode() == HttpStatus.OK){
                    Demand demand = responseEntityDemand.getBody();
                    if(demand.getOrganizationId() != null) {
                        if(demand.getOrganizationId().equals(organizationId)) {
                            ApiFuture<WriteResult> writeResult = db.collection(COLLECTION_NAME).document(testimonyId).delete();
                            return ResponseEntity.ok(testimonyId);
                        }
                        else{
                            throw new com.infinity.servicetestimony.exceptions.BadRequestException("Vous n'ête pas autorisé à supprimer cette demande");
                        }
                    }
                    else{
                        throw new com.infinity.servicetestimony.exceptions.NotFoundException("Cet organisation n'a pas créer cette demande");
                    }
                }
                else{
                    throw new com.infinity.servicetestimony.exceptions.NotFoundException("Cette demande n'existe pas");
                }
            }
            else{
                throw new com.infinity.servicetestimony.exceptions.NotFoundException("Cet organisation n'existe pas");
            }
        }
        catch (Exception e) {
            throw new com.infinity.servicetestimony.exceptions.InternalServerException(e.getMessage());
        }
    }
    public ResponseEntity<String> deleteDemandByUser(String userId, String testimonyId) throws InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        try {
            ResponseEntity<User> responseEntity = userServiceRestClient.getUserById(userId);
            if(responseEntity.getStatusCode() == HttpStatus.OK) {
                ResponseEntity<Demand> responseEntityDemand  = getDemandById(testimonyId);
                if(responseEntityDemand.getStatusCode() == HttpStatus.OK){
                    Demand demand = responseEntityDemand.getBody();
                    if(demand.getUserId() != null) {
                        if(demand.getOrganizationId().equals(userId)) {
                            ApiFuture<WriteResult> writeResult = db.collection(COLLECTION_NAME).document(testimonyId).delete();
                            return ResponseEntity.ok(testimonyId);
                        }
                        else{
                            throw new com.infinity.servicetestimony.exceptions.BadRequestException("Vous n'ête pas autorisé à supprimer cette demande");
                        }
                    }
                    else{
                        throw new com.infinity.servicetestimony.exceptions.NotFoundException("Cet organisation n'a pas créer cette demande");
                    }
                }
                else{
                    throw new com.infinity.servicetestimony.exceptions.NotFoundException("Cet temoignage n'existe pas");
                }
            }
            else{
                throw new com.infinity.servicetestimony.exceptions.NotFoundException("Cet utilisateur n'existe pas");
            }
        }
        catch (Exception e) {
            throw new com.infinity.servicetestimony.exceptions.InternalServerException(e.getMessage());
        }
    }


}
