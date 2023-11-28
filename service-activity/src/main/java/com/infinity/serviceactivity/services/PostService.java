package com.infinity.serviceactivity.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.infinity.serviceactivity.exceptions.BadRequestException;
import com.infinity.serviceactivity.exceptions.InternalServerException;
import com.infinity.serviceactivity.exceptions.NotFoundException;
import com.infinity.serviceactivity.models.*;
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
public class PostService {
    private static final String COLLECTION_NAME = "posts";
    private static final String ACTIVITY_COLLECTION_NAME = "activities";
    private static final String ASSIGNMENT_COLLECTION_NAME = "assignments";
    private static final String ORGANIZATION_COLLECTION_NAME = "organizations";

    @Autowired
    private AssignmentService assignmentService;


    public ResponseEntity<String> createPost(String organizationId, String activityId, Post post) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            // Verifiation de l'existance de l'activité
            DocumentReference docRefActivity = db.collection(ACTIVITY_COLLECTION_NAME).document(activityId);
            if(docRefActivity.get().get().exists()){
                Activity activity = docRefActivity.get().get().toObject(Activity.class);
                DocumentReference docRefAssignment = db.collection(ASSIGNMENT_COLLECTION_NAME).document(activity.getAssignmentId());
                if(docRefAssignment.get().get().exists()){
                    Assignment assignment = docRefAssignment.get().get().toObject(Assignment.class);
                    if(assignment.getOrganizationId().equals(organizationId)){
                        post.setActivityId(activityId);
                        ApiFuture<DocumentReference> docRef = db.collection(COLLECTION_NAME).add(post);
                        post.setPostId(docRef.get().getId());
                        docRef.get().update("postId", post.getPostId());

                        URI location = ServletUriComponentsBuilder.
                                fromCurrentContextPath().path("{postId}").
                                buildAndExpand(post.getPostId()).toUri();

                        return ResponseEntity.created(location).body(post.getPostId());
                    }
                    else{
                        throw new BadRequestException("Cette organisation n'est pas autorisé à créer un post avec cette mission");
                    }
                }
                else{
                    throw new NotFoundException("Mission non trouvé");
                }
            }
            else{
                throw new NotFoundException("Activité non trouvé");
            }
        }catch (Exception e){
            throw new InternalServerException(e.getMessage());
        }
    }
    public List<Post> getPostsByCauses(List<String>  causeIds) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            CollectionReference postsRef = db.collection(COLLECTION_NAME);

            Query query = postsRef.whereIn("causeId", causeIds);

            ApiFuture<QuerySnapshot> querySnapshot = query.get();

            List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
            return documents.stream().map(document -> document.toObject(Post.class)).toList();
        }
        catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Post> getAllPosts() {
        Firestore db = FirestoreClient.getFirestore();

        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("deleted", false)
                    .orderBy("creationDate", Query.Direction.DESCENDING).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            return documents.stream().map(document -> document.toObject(Post.class)).toList();

        }catch (Exception e){
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<Post> getPostById(String postId) throws InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        try {
            DocumentReference docRef = db.collection(COLLECTION_NAME).document(postId);
            ApiFuture<DocumentSnapshot> future = docRef.get();

            DocumentSnapshot document = future.get();
            if (document.exists()) {
                Post post = document.toObject(Post.class);
                return ResponseEntity.ok(post);
            }
            else {
                throw new NotFoundException("Post non trouvé");
            }
        }catch (Exception e){
            throw new InterruptedException(e.getMessage());
        }
    }

    public ResponseEntity<Post> patchPostInfo(String organizationId, String postId, Map<String, Object> postPatchInfo) {
        Firestore db = FirestoreClient.getFirestore();
        // Mise à jour des informations d'un post
        try {
            ResponseEntity<Post> responseEntity = getPostById(postId);
            if(responseEntity.getStatusCode() == HttpStatus.OK) {
                Post post = responseEntity.getBody();
                if (post != null){
                    DocumentReference docRefActivity = db.collection(ACTIVITY_COLLECTION_NAME).document(post.getActivityId());
                    if(docRefActivity.get().get().exists()){
                        Activity activity = docRefActivity.get().get().toObject(Activity.class);
                        DocumentReference docRefAssignment = db.collection(ASSIGNMENT_COLLECTION_NAME).document(activity.getAssignmentId());
                        if(docRefAssignment.get().get().exists()){
                            Assignment assignment = docRefAssignment.get().get().toObject(Assignment.class);
                            if(assignment.getOrganizationId().equals(organizationId)){
                                post.setMessage((String) postPatchInfo.getOrDefault("message", post.getMessage()));
                                post.setVideoUrl((String) postPatchInfo.getOrDefault("videoUrl", post.getVideoUrl()));
                                post.setImageUrls((List<String>) postPatchInfo.getOrDefault("imageUrls", post.getImageUrls()));

                                // Enregistrement de la modification dans la base de données
                                return updatePost(post, postId);
                            }
                            else{
                                throw new BadRequestException("Cette organisation n'est pas autorisé à modifier ce post");
                            }
                        }
                        else{
                            throw new NotFoundException("Mission non trouvé");
                        }
                    }
                    else {
                        throw new NotFoundException("Activité non trouvé");
                    }
                }
                else{
                    throw new NotFoundException("Post non trouvé");
                }
            }
            throw new NotFoundException("Post non trouvé");
        }catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<Post> updatePost(Post post, String postId) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            ResponseEntity<Post> responseEntity = getPostById(postId);
            if (responseEntity.getStatusCode() == HttpStatus.OK){
                Post postExist = responseEntity.getBody();
                if(postExist != null){
                    db.collection(COLLECTION_NAME).document(post.getPostId()).set(post);
                    return ResponseEntity.ok(post);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public List<Post> getPostsByActivityId(String activityId) {
        Firestore db = FirestoreClient.getFirestore();

        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("deleted", false)
                    .whereEqualTo("activityId", activityId)
                    .orderBy("creationDate", Query.Direction.DESCENDING)
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            return documents.stream().map(document -> document.toObject(Post.class)).toList();

        }catch (Exception e){
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseEntity<String> deletePost(String organizationId, String postId) {
        Firestore db = FirestoreClient.getFirestore();

        try {
            DocumentReference docRefPost = db.collection(COLLECTION_NAME).document(postId);

            if (docRefPost.get().get().exists()) {
                Post post = docRefPost.get().get().toObject(Post.class);
                DocumentReference docRefActivity = db.collection(ACTIVITY_COLLECTION_NAME).document(post.getActivityId());

                if(docRefActivity.get().get().exists()) {
                    Activity activity = docRefActivity.get().get().toObject(Activity.class);
                    DocumentReference docRefAssignment = db.collection(ASSIGNMENT_COLLECTION_NAME).document(activity.getAssignmentId());
                    if(docRefAssignment.get().get().exists()) {
                        Assignment assignment = docRefAssignment.get().get().toObject(Assignment.class);
                        if(assignment.getOrganizationId().equals(organizationId)){
                            docRefPost.update("deleted", true);
                            return ResponseEntity.ok(postId);
                        }
                        else {
                            throw new BadRequestException("Cette organisation n'est pas autorisé à modifier ce post");
                        }
                    }
                    else{
                        throw new NotFoundException("La mission correspondant à cette activité n'existe pas");
                    }
                }
                else{
                    throw new BadRequestException("Cette activité n'existe pas");
                }
            }
            else{
                throw new NotFoundException("Post non trouvé");
            }
        }catch (Exception e){
            throw new InternalServerException(e.getMessage());
        }
    }

    public List<Post> getAllPostsForOrganization(String organizationId) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            List<Assignment>  assignments = assignmentService.getAllAssignmentsByOrganizationId(organizationId);

            ApiFuture<QuerySnapshot> futureActivity = db.collection(ACTIVITY_COLLECTION_NAME)
                    .whereEqualTo("deleted", false)
                    .whereIn("assignmentId", assignments)
                    .orderBy("creationDate", Query.Direction.DESCENDING).get();
            List<QueryDocumentSnapshot> documentsActivities = futureActivity.get().getDocuments();
            List<Activity> activities = documentsActivities.stream().map(document -> document.toObject(Activity.class)).toList();


            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("deleted", false)
                    .whereIn("activityId", activities)
                    .orderBy("creationDate", Query.Direction.DESCENDING).get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            return documents.stream().map(document -> document.toObject(Post.class)).toList();
        }catch (Exception e){
            throw new InternalServerException(e.getMessage());
        }
    }
}
