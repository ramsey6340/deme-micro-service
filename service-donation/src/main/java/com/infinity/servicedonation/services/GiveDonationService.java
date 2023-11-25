package com.infinity.servicedonation.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.infinity.servicedonation.feign.MethodPaymentServiceRestClient;
import com.infinity.servicedonation.feign.OrganizationServiceRestClient;
import com.infinity.servicedonation.feign.UserServiceRestClient;
import com.infinity.servicedonation.models.*;
import com.infinity.servicetestimony.exceptions.BadRequestException;
import com.infinity.servicetestimony.exceptions.InternalServerException;
import com.infinity.servicetestimony.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Service
public class GiveDonationService {
    public static final String COLLECTION_NAME = "donations";
    @Autowired
    private OrganizationServiceRestClient organizationServiceRestClient;
    @Autowired
    private MethodPaymentServiceRestClient methodPaymentServiceRestClient;
    @Autowired
    private UserServiceRestClient userServiceRestClient;

    public ResponseEntity<FinancialDonation> createFinancialDonationByOrganizationToOrganization(
            String organizationId, String beneficiaryId, FinancialDonation financialDonation)    {
        try{
            Firestore db = FirestoreClient.getFirestore();
            ResponseEntity<Organization> organizationResponseEntity = organizationServiceRestClient.getOrganizationById(organizationId);
            if(organizationResponseEntity.getStatusCode() == HttpStatus.OK && !organizationId.equals(beneficiaryId)) {
                financialDonation.setDonorOrganizationId(organizationId);
                financialDonation.setDonorUserId(null);
                 ResponseEntity<MethodPayment> methodPaymentResponseEntity = methodPaymentServiceRestClient.getMethodPaymentById(financialDonation.getMethodPaymentId());
                 if(methodPaymentResponseEntity.getStatusCode() ==  HttpStatus.OK) {
                     if(financialDonation.getAmount() > 0) {
                         ResponseEntity<Organization> beneficiaryResponseEntity = organizationServiceRestClient.getOrganizationById(beneficiaryId);
                         if(beneficiaryResponseEntity.getStatusCode() == HttpStatus.OK) {
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

    public ResponseEntity<FinancialDonation> createFinancialDonationByOrganizationToDemand(
            String organizationId, String beneficiaryId, FinancialDonation financialDonation) {
        try{
            Firestore db = FirestoreClient.getFirestore();
            ResponseEntity<Organization> organizationResponseEntity = organizationServiceRestClient.getOrganizationById(organizationId);
            if(organizationResponseEntity.getStatusCode() == HttpStatus.OK && !organizationId.equals(beneficiaryId)) {
                financialDonation.setDonorOrganizationId(organizationId);
                financialDonation.setDonorUserId(null);
                ResponseEntity<MethodPayment> methodPaymentResponseEntity = methodPaymentServiceRestClient.getMethodPaymentById(financialDonation.getMethodPaymentId());
                if(methodPaymentResponseEntity.getStatusCode() ==  HttpStatus.OK) {
                    if(financialDonation.getAmount() > 0) {
                        ResponseEntity<Organization> beneficiaryResponseEntity = organizationServiceRestClient.getOrganizationById(beneficiaryId);
                        if(beneficiaryResponseEntity.getStatusCode() == HttpStatus.OK) {
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

    public ResponseEntity<FinancialDonation> createFinancialDonation(FinancialDonation financialDonation) {
        try{
            Firestore  db = FirestoreClient.getFirestore();
            ApiFuture<DocumentReference> docRef = db.collection(COLLECTION_NAME).add(financialDonation);
            financialDonation.setDonationId(docRef.get().getId());

            docRef.get().update("donationId", financialDonation.getDonationId());

            URI location = ServletUriComponentsBuilder.
                    fromCurrentContextPath().path("{donationId}").
                    buildAndExpand(financialDonation.getDonationId()).toUri();
            return ResponseEntity.created(location).body(financialDonation);
        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<FinancialDonation> createFinancialDonationByUserToOrganization(String userId, String beneficiaryId, FinancialDonation financialDonation) {
        try{
            Firestore db = FirestoreClient.getFirestore();
            ResponseEntity<User> userResponseEntity = userServiceRestClient.getUserById(userId);
            if(userResponseEntity.getStatusCode() == HttpStatus.OK && !userId.equals(beneficiaryId)) {
                financialDonation.setDonorUserId(userId);
                financialDonation.setDonorOrganizationId(null);
                ResponseEntity<MethodPayment> methodPaymentResponseEntity = methodPaymentServiceRestClient.getMethodPaymentById(financialDonation.getMethodPaymentId());
                if(methodPaymentResponseEntity.getStatusCode() ==  HttpStatus.OK) {
                    if(financialDonation.getAmount() > 0) {
                        ResponseEntity<Organization> beneficiaryResponseEntity = organizationServiceRestClient.getOrganizationById(beneficiaryId);
                        if(beneficiaryResponseEntity.getStatusCode() == HttpStatus.OK) {
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

    public ResponseEntity<FinancialDonation> createFinancialDonationByUserToDemand(String userId, String beneficiaryId, FinancialDonation financialDonation) {
        try{
            Firestore db = FirestoreClient.getFirestore();
            ResponseEntity<User> userResponseEntity = userServiceRestClient.getUserById(userId);
            if(userResponseEntity.getStatusCode() == HttpStatus.OK && !userId.equals(beneficiaryId)) {
                financialDonation.setDonorUserId(userId);
                financialDonation.setDonorOrganizationId(null);
                ResponseEntity<MethodPayment> methodPaymentResponseEntity = methodPaymentServiceRestClient.getMethodPaymentById(financialDonation.getMethodPaymentId());
                if(methodPaymentResponseEntity.getStatusCode() ==  HttpStatus.OK) {
                    if(financialDonation.getAmount() > 0) {
                        ResponseEntity<Organization> beneficiaryResponseEntity = organizationServiceRestClient.getOrganizationById(beneficiaryId);
                        if(beneficiaryResponseEntity.getStatusCode() == HttpStatus.OK) {
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

    public ResponseEntity<MaterialDonation> createMaterialDonationByUserToDemand(String userId, String beneficiaryId, FinancialDonation financialDonation) {
        return null;
    }

    public ResponseEntity<MaterialDonation> createMaterialDonationByOrganizationToOrganization(String organizationId, String beneficiaryId, FinancialDonation financialDonation) {
        return null;
    }

    public ResponseEntity<MaterialDonation> createMaterialDonationByOrganizationToDemand(String organizationId, String beneficiaryId, FinancialDonation financialDonation) {
        return null;
    }

    public ResponseEntity<MaterialDonation> createMaterialDonationByUserToOrganization(String userId, String beneficiaryId, FinancialDonation financialDonation) {
        return null;
    }
}
