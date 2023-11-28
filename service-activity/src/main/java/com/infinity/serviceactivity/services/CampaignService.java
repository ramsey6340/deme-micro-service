package com.infinity.serviceactivity.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.infinity.serviceactivity.exceptions.BadRequestException;
import com.infinity.serviceactivity.exceptions.InternalServerException;
import com.infinity.serviceactivity.exceptions.NotFoundException;
import com.infinity.serviceactivity.models.Activity;
import com.infinity.serviceactivity.models.Assignment;
import com.infinity.serviceactivity.models.Campaign;
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
public class CampaignService {

    private static final String COLLECTION_NAME = "campaigns";
    private static final String ORGANIZATION_COLLECTION_NAME = "organizations";
    private static final String ASSIGNMENT_COLLECTION_NAME = "assignments";
    @Autowired
    private AssignmentService assignmentService;

    public ResponseEntity<String> createCampaign(String organizationId, String assignmentId, Campaign campaign) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            DocumentReference docRefAssignment = db.collection(ASSIGNMENT_COLLECTION_NAME).document(assignmentId);
            if(docRefAssignment.get().get().exists()) {
                Assignment assignment = docRefAssignment.get().get().toObject(Assignment.class);
                if (assignment.getOrganizationId().equals(organizationId)){
                    campaign.setAssignmentId(assignmentId);
                    ApiFuture<DocumentReference> docRef = db.collection(COLLECTION_NAME).add(campaign);
                    campaign.setCampaignId(docRef.get().getId());
                    docRef.get().update("campaignId", campaign.getCampaignId());

                    URI location = ServletUriComponentsBuilder.
                            fromCurrentContextPath().path("{campaignId}").
                            buildAndExpand(campaign.getCampaignId()).toUri();

                    return ResponseEntity.created(location).body(campaign.getCampaignId());
                }
                else{
                    throw new BadRequestException("Cette organisation n'est pas autorisé à créer une campagne avec cette mission");
                }
            }
            else{
                throw new NotFoundException("Mission non trouvé");
            }
        }catch (Exception e){
            throw new InternalServerException(e.getMessage());
        }

    }

    public List<Campaign> getAllCampaigns() {
        Firestore db = FirestoreClient.getFirestore();
        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("deleted", false)
                    .orderBy("creationDate", Query.Direction.DESCENDING).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            return documents.stream().map(document -> document.toObject(Campaign.class)).toList();
        }catch (Exception e){
            throw new InternalServerException(e.getMessage());
        }
    }

    public List<Campaign> getAllCampaignsForOrganization(String organizationId) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            List<Assignment>  assignments = assignmentService.getAllAssignmentsByOrganizationId(organizationId);

            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("deleted", false)
                    .whereIn("assignmentId", assignments)
                    .orderBy("creationDate", Query.Direction.DESCENDING).get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            return documents.stream().map(document -> document.toObject(Campaign.class)).toList();
        }catch (Exception e){
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<Campaign> getCampaignById(String campaignId) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            DocumentReference docRefCampaign = db.collection(COLLECTION_NAME).document(campaignId);
            if (docRefCampaign.get().get().exists()) {
                Campaign campaign = docRefCampaign.get().get().toObject(Campaign.class);
                return ResponseEntity.ok(campaign);
            }
            else {
                throw new NotFoundException("Campagne non trouvé");
            }
        }catch (Exception e){
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<Campaign> patchCampaignInfo(String organizationId, String campaignId, Map<String, Object> campaignPatchInfo) {
        // Mise à jour des informations d'une mission
        Firestore db = FirestoreClient.getFirestore();
        try {
            ResponseEntity<Campaign> responseEntity = getCampaignById(campaignId);
            if(responseEntity.getStatusCode() == HttpStatus.OK) {
                Campaign campaign = responseEntity.getBody();
                if (campaign != null){
                    DocumentReference docRefAssignment = db.collection(ASSIGNMENT_COLLECTION_NAME).document(campaign.getAssignmentId());
                    if (docRefAssignment.get().get().exists()) {
                        Assignment assignment = docRefAssignment.get().get().toObject(Assignment.class);
                        if (assignment.getOrganizationId().equals(organizationId)) {
                            campaign.setTitle((String) campaignPatchInfo.getOrDefault("title", campaign.getTitle()));
                            campaign.setVideoUrl((String) campaignPatchInfo.getOrDefault("videoUrl", campaign.getVideoUrl()));
                            campaign.setDescription((String) campaignPatchInfo.getOrDefault("description", campaign.getDescription()));
                            campaign.setDeleted((boolean) campaignPatchInfo.getOrDefault("deleted", campaign.isDeleted()));
                            campaign.setAssignmentId((String) campaignPatchInfo.getOrDefault("assignmentId", campaign.getAssignmentId()));

                            // Enregistrement de la modification dans la base de données
                            return updateCampaign(campaign, campaignId);
                        }
                        else{
                            throw new BadRequestException("Cette organisation n'est pas autorisé à modifier cette campagne");
                        }
                    }
                    else{
                        throw new NotFoundException("Mission non trouvé");
                    }
                }
            }
            throw new NotFoundException("Campagne non trouvé");
        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<Campaign> updateCampaign(Campaign campaign, String campaignId) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            ResponseEntity<Campaign> responseEntity = getCampaignById(campaignId);
            if (responseEntity.getStatusCode() == HttpStatus.OK){
                Campaign campaignExist = responseEntity.getBody();
                if(campaignExist != null){
                    db.collection(COLLECTION_NAME).document(campaignExist.getCampaignId()).set(campaign);
                    return ResponseEntity.ok(campaign);
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

    public ResponseEntity<String> deleteCampaign(String organizationId, String campaignId) {
        Firestore db = FirestoreClient.getFirestore();

        try {
            DocumentReference docRefCampaign = db.collection(COLLECTION_NAME).document(campaignId);

            if(docRefCampaign.get().get().exists()) {
                Campaign campaign = docRefCampaign.get().get().toObject(Campaign.class);
                DocumentReference docRefAssignment = db.collection(ASSIGNMENT_COLLECTION_NAME).document(campaign.getAssignmentId());
                if(docRefAssignment.get().get().exists()) {
                    Assignment assignment = docRefAssignment.get().get().toObject(Assignment.class);
                    DocumentReference docRefOrganization = db.collection(ORGANIZATION_COLLECTION_NAME).document(assignment.getOrganizationId());
                    if(docRefOrganization.get().get().exists()) {
                        Organization organization = docRefOrganization.get().get().toObject(Organization.class);
                        if(organization.getOrganizationId().equals(organizationId)){
                            docRefCampaign.update("deleted", true);
                            return ResponseEntity.ok(campaignId);
                        }
                        else {
                            throw new BadRequestException("Cet organisation n'est pas autorisé à supprimer cette campagne");
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
