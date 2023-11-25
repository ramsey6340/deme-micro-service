package com.infinity.serviceuser.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import com.infinity.serviceuser.exceptions.InternalServerException;
import com.infinity.serviceuser.exceptions.NotFoundException;
import com.infinity.serviceuser.models.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {
    public static final String COLLECTION_NAME = "users";


    public ResponseEntity<User> getUserById(String userId) throws ExecutionException, InterruptedException {
            Firestore db = FirestoreClient.getFirestore();
            try {
                DocumentReference docRef = db.collection(COLLECTION_NAME).document(userId);
                ApiFuture<DocumentSnapshot> future = docRef.get();

                DocumentSnapshot document = future.get();
                if (document.exists()) {
                    User user = document.toObject(User.class);
                    return ResponseEntity.ok(user);
                }
                else {
                    throw new NotFoundException("Utilisateur non trouvé");
                }
            }catch (Exception e){
                throw new InterruptedException(e.getMessage());
            }
        }

    public ResponseEntity<User> updateUser(User user, String userId) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            ResponseEntity<User> responseEntity = getUserById(userId);
            if (responseEntity.getStatusCode() == HttpStatus.OK){
                User userExist = responseEntity.getBody();
                if(userExist != null){
                    db.collection(COLLECTION_NAME).document(user.getUserId()).set(user);
                    return ResponseEntity.ok(user);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<User> patchUserInfo(String userId, Map<String, Object> userPatchInfo) {
        // Mise à jour des informations de l'utilisateur
        try {
            ResponseEntity<User> responseEntity = getUserById(userId);
            if(responseEntity.getStatusCode() == HttpStatus.OK) {
                User user = responseEntity.getBody();
                if (user != null){
                    user.setNumTel((String) userPatchInfo.getOrDefault("numTel", user.getNumTel()));
                    user.setActivated((boolean) userPatchInfo.getOrDefault("isActivated", user.isActivated()));
                    user.setName((String) userPatchInfo.getOrDefault("name", user.getName()));
                    user.setEmail((String) userPatchInfo.getOrDefault("email", user.getEmail()));
                    user.setLogin((String) userPatchInfo.getOrDefault("login", user.getLogin()));
                    user.setBirthDay((String) userPatchInfo.getOrDefault("birthDay", user.getBirthDay()));
                    user.setImageUrl((String) userPatchInfo.getOrDefault("imageUrl", user.getImageUrl()));
                    user.setDeviceType((String) userPatchInfo.getOrDefault("deviceType", user.getDeviceType()));
                    user.setAnonymous((boolean) userPatchInfo.getOrDefault("isAnonymous", user.isAnonymous()));
                    user.setDelete((boolean) userPatchInfo.getOrDefault("delete", user.isDelete()));
                    user.setProfile((String) userPatchInfo.getOrDefault("profile", user.getProfile()));
                    user.setGender((String) userPatchInfo.getOrDefault("gender", user.getGender()));

                    user.setPreferredPaymentMethods((List<String>) userPatchInfo.getOrDefault("preferredPaymentMethods", user.getPreferredPaymentMethods()));

                    user.setFavoriteHumanitarianCauses((List<String>) userPatchInfo.getOrDefault("favoriteHumanitarianCauses", user.getFavoriteHumanitarianCauses()));

                    // Enregistrement de la modification dans la base de données
                    return updateUser(user, userId);
                }
            }
            throw new NotFoundException("Utilisateur non trouvé");
        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public List<User> getAllUsers() {
        // Récuperer la  liste des utilisateurs
        Firestore db = FirestoreClient.getFirestore();
        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            return documents.stream().map(document -> document.toObject(User.class)).toList();

        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
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

}
