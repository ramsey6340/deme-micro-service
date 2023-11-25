package com.infinity.serviceauth.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.cloud.FirestoreClient;
import com.infinity.serviceauth.exceptions.InternalServerException;
import com.infinity.serviceauth.exceptions.NotFoundException;
import com.infinity.serviceauth.exceptions.RessourceExistantException;
import com.infinity.serviceauth.feign.EmailServiceRestClient;
import com.infinity.serviceauth.models.Organization;
import com.infinity.serviceauth.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.SecureRandom;

@Service
public class UserAuthService {
    public static final String COLLECTION_NAME = "users";
    public static final String COLLECTION_NAME_ORGANIZATION = "organizations";

    @Autowired
    private EmailServiceRestClient emailServiceRestClient;

    public ResponseEntity<String> createUser(String profile, String password, User user) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            // Enregistrement de l'utilisateur dans le service Auth
            CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(user.getEmail())
                    .setEmailVerified(true)
                    .setPassword(password)
                    .setDisplayName(profile)
                    .setDisabled(false);

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
            System.out.println("Successfully created new user: " + userRecord.getUid());
            System.out.println("Successfully created new user: " + userRecord.getDisplayName());

            user.setUserId(userRecord.getUid());
            ApiFuture<WriteResult> docRef = db.collection(COLLECTION_NAME).document(user.getUserId()).set(user);

            //DocumentReference updateDocRef = db.collection(COLLECTION_NAME).document(user.getUserId());
            //updateDocRef.update("userId", user.getUserId());

            URI location = ServletUriComponentsBuilder.
                    fromCurrentContextPath().path("{userId}").
                    buildAndExpand(user.getUserId()).toUri();

            return ResponseEntity.created(location).body(user.getUserId());

        }    catch (FirebaseAuthException e) {
            e.printStackTrace();
            throw new RessourceExistantException("Erreur lors de la création de l'utilisateur : " + e.getMessage());
        }
    }
    public ResponseEntity<String> createOrganization(String profile, String password, Organization organization) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            // Enregistrement de l'organisation dans le service Auth
            CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(organization.getEmail())
                    .setEmailVerified(true)
                    .setPassword(password)
                    .setDisplayName(profile)
                    .setDisabled(false);

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
            System.out.println("Successfully created new organization: " + userRecord.getUid());
            System.out.println("Successfully created new organization: " + userRecord.getDisplayName());

            organization.setOrganizationId(userRecord.getUid());
            ApiFuture<WriteResult> docRef = db.collection(COLLECTION_NAME_ORGANIZATION).document(organization.getOrganizationId()).set(organization);

            URI location = ServletUriComponentsBuilder.
                    fromCurrentContextPath().path("{organization}").
                    buildAndExpand(organization.getOrganizationId()).toUri();

            return ResponseEntity.created(location).body(organization.getOrganizationId());

        }    catch (FirebaseAuthException e) {
            e.printStackTrace();
            throw new RessourceExistantException("Erreur lors de la création de l'organisation : " + e.getMessage());
        }
    }

    public ResponseEntity<User> getUserByNumTelAndPassword(String numTel, String password) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            CollectionReference users = db.collection(COLLECTION_NAME);
            Query query = users.whereEqualTo("numTel", numTel).whereEqualTo("password", password);
            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            DocumentSnapshot document = querySnapshot.get().getDocuments().get(0);
            if(document.exists()){
                User user = document.toObject(User.class);
                return ResponseEntity.ok(user);
            }
            else {
                throw new NotFoundException("Utilisateur non trouvé");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<User> getUserByEmailAndPassword(String email, String password) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            CollectionReference users = db.collection(COLLECTION_NAME);
            Query query = users.whereEqualTo("email", email).whereEqualTo("password", password);
            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            DocumentSnapshot document = querySnapshot.get().getDocuments().get(0);
            if(document.exists()){
                User user = document.toObject(User.class);
                return ResponseEntity.ok(user);
            }
            else {
                throw new NotFoundException("Utilisateur non trouvé");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<String> sendMailOtpCode(String email) {
        String otpCode = generateOtpCode(4);
        emailServiceRestClient.sendEmail(email, "Vérification d'email", otpCode);
        return ResponseEntity.ok(otpCode);
    }

    public static String generateOtpCode(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("La longueur du code doit être positive.");
        }

        StringBuilder otp = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10);
            otp.append(digit);
        }

        return otp.toString();
    }

    public ResponseEntity<String> verifyEmailForResetPassword(String email) throws FirebaseAuthException {
        try{
            UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);
            if(userRecord.getUid() != null) {
                System.out.println("Successfully fetched user data: " + userRecord.getEmail());
                return ResponseEntity.ok(userRecord.getUid());
            }
            else{
                throw new NotFoundException("Utilisateur non trouvé");
            }
        }catch (Exception e){
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<String> resetPassword(String userId, String  newPassword) {
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
