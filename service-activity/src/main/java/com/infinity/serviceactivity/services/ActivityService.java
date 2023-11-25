package com.infinity.serviceactivity.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.infinity.serviceactivity.exceptions.InternalServerException;
import com.infinity.serviceactivity.exceptions.NotFoundException;
import com.infinity.serviceactivity.feign.OrganizationServiceRestClient;
import com.infinity.serviceactivity.models.Activity;
import com.infinity.serviceactivity.models.Organization;
import com.infinity.serviceactivity.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ActivityService {
    public static final String COLLECTION_NAME = "activities";

    @Autowired
    private OrganizationServiceRestClient organizationServiceRestClient;

    public List<Activity> getAllActivities() {
        Firestore db = FirestoreClient.getFirestore();

        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).orderBy("creationDate", Query.Direction.DESCENDING).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            return documents.stream().map(document -> document.toObject(Activity.class)).toList();

        }catch (Exception e){
            throw new InternalServerException(e.getMessage());
        }
    }
    public ResponseEntity<Activity> getActivityById(String activityId) throws InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        try {
            DocumentReference docRef = db.collection(COLLECTION_NAME).document(activityId);
            ApiFuture<DocumentSnapshot> future = docRef.get();

            DocumentSnapshot document = future.get();
            if (document.exists()) {
                Activity activity = document.toObject(Activity.class);
                return ResponseEntity.ok(activity);
            }
            else {
                throw new NotFoundException("Activité non trouvé");
            }
        }catch (Exception e){
            throw new InterruptedException(e.getMessage());
        }
    }
    public ResponseEntity<Activity> patchActivityInfo(String activityId, Map<String, Object> activityPatchInfo) {
        // Mise à jour des informations d'une activité
        try {
            ResponseEntity<Activity> responseEntity = getActivityById(activityId);
            if(responseEntity.getStatusCode() == HttpStatus.OK) {
                Activity activity = responseEntity.getBody();
                if (activity != null){
                    activity.setTitle((String) activityPatchInfo.getOrDefault("title", activity.getTitle()));
                    activity.setDescription((String) activityPatchInfo.getOrDefault("description", activity.getDescription()));
                    activity.setStartDate((String) activityPatchInfo.getOrDefault("startDate", activity.getStartDate()));
                    activity.setEndDate((String) activityPatchInfo.getOrDefault("endDate", activity.getEndDate()));

                    // Enregistrement de la modification dans la base de données
                    return updateActivity(activity, activityId);
                }
            }
            throw new NotFoundException("Activité non trouvé");
        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<Activity> updateActivity(Activity activity, String activityId) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            ResponseEntity<Activity> responseEntity = getActivityById(activityId);
            if (responseEntity.getStatusCode() == HttpStatus.OK){
                Activity activityExist = responseEntity.getBody();
                if(activityExist != null){
                    db.collection(COLLECTION_NAME).document(activity.getActivityId()).set(activity);
                    return ResponseEntity.ok(activity);
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

    public List<Activity> getAllActivitiesByOrganizationId(String organizationId) {
        Firestore db = FirestoreClient.getFirestore();

        try {
            ResponseEntity<Organization> responseEntity = organizationServiceRestClient.getOrganizationById(organizationId);
           if(responseEntity.getStatusCode() == HttpStatus.OK){
               ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                       .orderBy("creationDate", Query.Direction.DESCENDING)
                       .whereEqualTo("organizationId", organizationId).get();

               List<QueryDocumentSnapshot> documents = future.get().getDocuments();
               return documents.stream().map(document -> document.toObject(Activity.class)).toList();
           }
           else{
               throw new NotFoundException("Organisation non trouvé");
           }

        }catch (Exception e){
            throw new InternalServerException(e.getMessage());
        }
    }
}
