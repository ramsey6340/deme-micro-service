package com.infinity.servicedonation.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.infinity.servicedonation.exceptions.BadRequestException;
import com.infinity.servicedonation.exceptions.InternalServerException;
import com.infinity.servicedonation.exceptions.NotFoundException;
import com.infinity.servicedonation.feign.MethodPaymentServiceRestClient;
import com.infinity.servicedonation.feign.OrganizationServiceRestClient;
import com.infinity.servicedonation.feign.UserServiceRestClient;
import com.infinity.servicedonation.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
public class GiveDonationService {
    public static final String COLLECTION_NAME = "donations";
    public static final String ORGANIZATION_COLLECTION_NAME = "organizations";
    public static final String METHOD_PAYMENT_COLLECTION_NAME = "methodPayments";
    public static final String DEMAND_COLLECTION_NAME = "demands";
    public static final String USER_COLLECTION_NAME = "users";


    public ResponseEntity<String> createFinancialDonationByOrganizationToOrganization(
            String organizationId, String beneficiaryId, FinancialDonation financialDonation)    {
        try{
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference docRefOrganization = db.collection(ORGANIZATION_COLLECTION_NAME).document(organizationId);

            if(docRefOrganization.get().get().exists() && !organizationId.equals(beneficiaryId)) {
                financialDonation.setDonorOrganizationId(organizationId);
                financialDonation.setDonorUserId(null);
                DocumentReference docRefMethodPayment = db.collection(METHOD_PAYMENT_COLLECTION_NAME).document(financialDonation.getMethodPaymentId());

                if(docRefMethodPayment.get().get().exists()) {
                     if(financialDonation.getAmount() > 0) {
                         DocumentReference docRefBeneficiary = db.collection(ORGANIZATION_COLLECTION_NAME).document(beneficiaryId);

                         if(docRefBeneficiary.get().get().exists()) {
                             financialDonation.setBeneficiaryOrganizationId(beneficiaryId);
                             financialDonation.setBeneficiaryDemandId(null);

                             return createFinancialDonation(financialDonation);
                         }
                         else{
                             throw new NotFoundException("L'organisation beneficaire est introuvable");
                         }
                     }
                     else {
                         throw new BadRequestException("Le montant de la donation doit être positif");
                     }
                 }
                 else {
                     throw new NotFoundException("Cette methode de paiement n'existe pas");
                 }
            }
            else {
                throw new NotFoundException("Cette organisation n'existe pas ou ne peut pas faire de don à lui même");
            }
        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<String> createFinancialDonationByOrganizationToDemand(
            String organizationId, String beneficiaryId, FinancialDonation financialDonation) {
        try{
            Firestore db = FirestoreClient.getFirestore();

            DocumentReference docRefOrganization = db.collection(ORGANIZATION_COLLECTION_NAME).document(organizationId);
            if(docRefOrganization.get().get().exists() && !organizationId.equals(beneficiaryId)) {
                financialDonation.setDonorOrganizationId(organizationId);
                financialDonation.setDonorUserId(null);

                DocumentReference docRefMethodPayment = db.collection(METHOD_PAYMENT_COLLECTION_NAME).document(financialDonation.getMethodPaymentId());
                if(docRefMethodPayment.get().get().exists()) {
                    if(financialDonation.getAmount() > 0) {
                        DocumentReference docRefDemand = db.collection(DEMAND_COLLECTION_NAME).document(beneficiaryId);
                        if(docRefDemand.get().get().exists()) {
                            financialDonation.setBeneficiaryOrganizationId(null);
                            financialDonation.setBeneficiaryDemandId(beneficiaryId);

                            return createFinancialDonation(financialDonation);
                        }
                        else{
                            throw new NotFoundException("la demande beneficaire est introuvable");
                        }
                    }
                    else {
                        throw new BadRequestException("Le montant de la donation doit être positif");
                    }
                }
                else {
                    throw new NotFoundException("Cette methode de paiement n'existe pas");
                }
            }
            else {
                throw new NotFoundException("Cette organisation n'existe pas ou ne peut pas faire de don à lui même");
            }
        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<String> createFinancialDonation(FinancialDonation financialDonation) {
        try{
            Firestore  db = FirestoreClient.getFirestore();
            ApiFuture<DocumentReference> docRef = db.collection(COLLECTION_NAME).add(financialDonation);
            financialDonation.setDonationId(docRef.get().getId());

            docRef.get().update("donationId", financialDonation.getDonationId());

            URI location = ServletUriComponentsBuilder.
                    fromCurrentContextPath().path("{donationId}").
                    buildAndExpand(financialDonation.getDonationId()).toUri();
            return ResponseEntity.created(location).body(financialDonation.getDonationId());
        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }
    public ResponseEntity<MaterialDonation> createMaterialDonation(MaterialDonation materialDonation) {
        try{
            Firestore  db = FirestoreClient.getFirestore();
            ApiFuture<DocumentReference> docRef = db.collection(COLLECTION_NAME).add(materialDonation);
            materialDonation.setDonationId(docRef.get().getId());

            docRef.get().update("donationId", materialDonation.getDonationId());

            URI location = ServletUriComponentsBuilder.
                    fromCurrentContextPath().path("{donationId}").
                    buildAndExpand(materialDonation.getDonationId()).toUri();
            return ResponseEntity.created(location).body(materialDonation);
        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<String> createFinancialDonationByUserToOrganization(String userId, String beneficiaryId, FinancialDonation financialDonation) {
        try{
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference docRefUser = db.collection(USER_COLLECTION_NAME).document(userId);
            if(docRefUser.get().get().exists() && !userId.equals(beneficiaryId)) {
                financialDonation.setDonorUserId(userId);
                financialDonation.setDonorOrganizationId(null);
                DocumentReference docRefMethodPayment = db.collection(METHOD_PAYMENT_COLLECTION_NAME).document(financialDonation.getMethodPaymentId());
                if(docRefMethodPayment.get().get().exists()) {
                    if(financialDonation.getAmount() > 0) {
                        DocumentReference docRefBeneficiary = db.collection(ORGANIZATION_COLLECTION_NAME).document(beneficiaryId);
                        if(docRefBeneficiary.get().get().exists()) {
                            financialDonation.setBeneficiaryOrganizationId(beneficiaryId);
                            financialDonation.setBeneficiaryDemandId(null);

                            return createFinancialDonation(financialDonation);
                        }
                        else{
                            throw new NotFoundException("L'organisation beneficaire est introuvable");
                        }
                    }
                    else {
                        throw new BadRequestException("Le montant de la donation doit être positif");
                    }
                }
                else {
                    throw new NotFoundException("Cette methode de paiement n'existe pas");
                }
            }
            else {
                throw new NotFoundException("Cet user n'existe pas ou ne peut pas faire de don à lui même");
            }
        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<String> createFinancialDonationByUserToDemand(String userId, String beneficiaryId, FinancialDonation financialDonation) {
        try{
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference docRefUser = db.collection(USER_COLLECTION_NAME).document(userId);
            if(docRefUser.get().get().exists() && !userId.equals(beneficiaryId)) {
                financialDonation.setDonorUserId(userId);
                financialDonation.setDonorOrganizationId(null);
                DocumentReference docRefMethodPayment = db.collection(METHOD_PAYMENT_COLLECTION_NAME).document(financialDonation.getMethodPaymentId());
                if(docRefMethodPayment.get().get().exists()) {
                    if(financialDonation.getAmount() > 0) {
                        DocumentReference docRefDemand = db.collection(DEMAND_COLLECTION_NAME).document(beneficiaryId);
                        if(docRefDemand.get().get().exists()) {
                            financialDonation.setBeneficiaryOrganizationId(null);
                            financialDonation.setBeneficiaryDemandId(beneficiaryId);

                            return createFinancialDonation(financialDonation);
                        }
                        else{
                            throw new NotFoundException("La demande beneficaire est introuvable");
                        }
                    }
                    else {
                        throw new BadRequestException("Le montant de la donation doit être positif");
                    }
                }
                else {
                    throw new NotFoundException("Cette methode de paiement n'existe pas");
                }
            }
            else {
                throw new NotFoundException("Cet User n'existe pas ou ne peut pas faire de don à lui même");
            }
        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    /*===================================Donation Materiel==============================*/

    public ResponseEntity<MaterialDonation> createMaterialDonationByUserToDemand(
            String userId, String beneficiaryId, MaterialDonation materialDonation) {

        try{
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference docRefUser = db.collection(USER_COLLECTION_NAME).document(userId);
            if(docRefUser.get().get().exists() && !userId.equals(beneficiaryId)) {
                materialDonation.setDonorUserId(userId);
                materialDonation.setDonorOrganizationId(null);
                if(!materialDonation.getDescriptionMaterialDonation().isEmpty()) {
                    DocumentReference docRefDemand = db.collection(DEMAND_COLLECTION_NAME).document(beneficiaryId);
                    if(docRefDemand.get().get().exists()) {
                        materialDonation.setBeneficiaryOrganizationId(null);
                        materialDonation.setBeneficiaryDemandId(beneficiaryId);

                        return createMaterialDonation(materialDonation);
                    }
                    else{
                        throw new NotFoundException("La demande beneficaire est introuvable");
                    }
                }
                else {
                    throw new BadRequestException("La description de ladonation ne doit pas être vide");
                }
            }
            else {
                throw new NotFoundException("Cet User n'existe pas ou ne peut pas faire de don à lui même");
            }
        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<MaterialDonation> createMaterialDonationByOrganizationToOrganization(String organizationId, String beneficiaryId, MaterialDonation materialDonation) {
        try{
            Firestore db = FirestoreClient.getFirestore();

            DocumentReference docRefOrganization = db.collection(ORGANIZATION_COLLECTION_NAME).document(organizationId);
            if(docRefOrganization.get().get().exists() && !organizationId.equals(beneficiaryId)) {
                materialDonation.setDonorOrganizationId(organizationId);
                materialDonation.setDonorUserId(null);
                if(!materialDonation.getDescriptionMaterialDonation().isEmpty()) {
                    DocumentReference docRefBeneficiary = db.collection(ORGANIZATION_COLLECTION_NAME).document(beneficiaryId);
                    if(docRefBeneficiary.get().get().exists()) {
                        materialDonation.setBeneficiaryOrganizationId(beneficiaryId);
                        materialDonation.setBeneficiaryDemandId(null);

                        return createMaterialDonation(materialDonation);
                    }
                    else{
                        throw new NotFoundException("L'organisation beneficaire est introuvable");
                    }
                }
                else {
                    throw new BadRequestException("La description de la donation ne doit pas être null");
                }
            }
            else {
                throw new NotFoundException("Cette organisation n'existe pas ou ne peut pas faire de don à lui même");
            }
        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<MaterialDonation> createMaterialDonationByOrganizationToDemand(String organizationId, String beneficiaryId, MaterialDonation materialDonation) {
        try{
            Firestore db = FirestoreClient.getFirestore();

            DocumentReference docRefOrganization = db.collection(ORGANIZATION_COLLECTION_NAME).document(organizationId);
            if(docRefOrganization.get().get().exists() && !organizationId.equals(beneficiaryId)) {
                materialDonation.setDonorOrganizationId(organizationId);
                materialDonation.setDonorUserId(null);
                if(!materialDonation.getDescriptionMaterialDonation().isEmpty()) {
                    DocumentReference docRefDemand = db.collection(DEMAND_COLLECTION_NAME).document(beneficiaryId);
                    if(docRefDemand.get().get().exists()) {
                        materialDonation.setBeneficiaryOrganizationId(null);
                        materialDonation.setBeneficiaryDemandId(beneficiaryId);

                        return createMaterialDonation(materialDonation);
                    }
                    else{
                        throw new NotFoundException("la demande beneficaire est introuvable");
                    }
                }
                else {
                    throw new BadRequestException("La description de la donation ne doit pas être null");
                }
            }
            else {
                throw new NotFoundException("Cette organisation n'existe pas ou ne peut pas faire de don à lui même");
            }
        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<MaterialDonation> createMaterialDonationByUserToOrganization(String userId, String beneficiaryId, MaterialDonation materialDonation) {
        try{
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference docRefUser = db.collection(USER_COLLECTION_NAME).document(userId);
            if(docRefUser.get().get().exists() && !userId.equals(beneficiaryId)) {
                materialDonation.setDonorUserId(userId);
                materialDonation.setDonorOrganizationId(null);
                if(!materialDonation.getDescriptionMaterialDonation().isEmpty()) {
                    DocumentReference docRefBeneficiary = db.collection(ORGANIZATION_COLLECTION_NAME).document(beneficiaryId);
                    if(docRefBeneficiary.get().get().exists()) {
                        materialDonation.setBeneficiaryOrganizationId(beneficiaryId);
                        materialDonation.setBeneficiaryDemandId(null);

                        return createMaterialDonation(materialDonation);
                    }
                    else{
                        throw new NotFoundException("L'organisation beneficaire est introuvable");
                    }
                }
                else {
                    throw new BadRequestException("La description de la demande ne doit pas être null");
                }
            }
            else {
                throw new NotFoundException("Cet user n'existe pas ou ne peut pas faire de don à lui même");
            }
        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public List<FinancialDonation> getAllFinancialDonation() {
        Firestore db = FirestoreClient.getFirestore();
        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("deleted", false)
                    .select("amount")
                    .orderBy("creationDate", Query.Direction.DESCENDING).get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            return documents.stream().map(document -> document.toObject(FinancialDonation.class)).toList();


        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }
    public List<MaterialDonation> getAllMaterialDonation() {
        Firestore db = FirestoreClient.getFirestore();
        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("deleted", false)
                    .select("descriptionMaterialDonation")
                    .orderBy("creationDate", Query.Direction.DESCENDING).get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            return documents.stream().map(document -> document.toObject(MaterialDonation.class)).toList();


        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public List<MaterialDonation> getAllMaterialDonationMadeByUser(String userId) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("deleted", false)
                    .whereEqualTo("donorUserId", userId)
                    .select("descriptionMaterialDonation")
                    .orderBy("creationDate", Query.Direction.DESCENDING).get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            return documents.stream().map(document -> document.toObject(MaterialDonation.class)).toList();


        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public List<MaterialDonation> getAllMaterialDonationMadeByOrganization(String organizationId) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("deleted", false)
                    .whereEqualTo("donorOrganizationId", organizationId)
                    .select("descriptionMaterialDonation")
                    .orderBy("creationDate", Query.Direction.DESCENDING).get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            return documents.stream().map(document -> document.toObject(MaterialDonation.class)).toList();


        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public List<FinancialDonation> getAllFinancialDonationMadeByUser(String userId) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("deleted", false)
                    .whereEqualTo("donorUserId", userId)
                    .select("amount")
                    .orderBy("creationDate", Query.Direction.DESCENDING).get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            return documents.stream().map(document -> document.toObject(FinancialDonation.class)).toList();


        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public List<FinancialDonation> getAllFinancialDonationMadeByOrganization(String organizationId) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("deleted", false)
                    .whereEqualTo("donorOrganizationId", organizationId)
                    .select("amount")
                    .orderBy("creationDate", Query.Direction.DESCENDING).get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            return documents.stream().map(document -> document.toObject(FinancialDonation.class)).toList();


        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }


    public List<MaterialDonation> getAllMaterialDonationReceivedByDemand(String userId) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("deleted", false)
                    .whereEqualTo("beneficiaryDemandId", userId)
                    .select("descriptionMaterialDonation")
                    .orderBy("creationDate", Query.Direction.DESCENDING).get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            return documents.stream().map(document -> document.toObject(MaterialDonation.class)).toList();


        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public List<MaterialDonation> getAllMaterialDonationReceivedByOrganization(String organizationId) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("deleted", false)
                    .whereEqualTo("beneficiaryOrganizationId", organizationId)
                    .select("descriptionMaterialDonation")
                    .orderBy("creationDate", Query.Direction.DESCENDING).get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            return documents.stream().map(document -> document.toObject(MaterialDonation.class)).toList();


        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public List<FinancialDonation> getAllFinancialDonationReceivedByDemand(String userId) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("deleted", false)
                    .whereEqualTo("beneficiaryDemandId", userId)
                    .select("amount")
                    .orderBy("creationDate", Query.Direction.DESCENDING).get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            return documents.stream().map(document -> document.toObject(FinancialDonation.class)).toList();


        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public List<FinancialDonation> getAllFinancialDonationReceivedByOrganization(String organizationId) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("deleted", false)
                    .whereEqualTo("beneficiaryOrganizationId", organizationId)
                    .select("amount")
                    .orderBy("creationDate", Query.Direction.DESCENDING).get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            return documents.stream().map(document -> document.toObject(FinancialDonation.class)).toList();


        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }
}
