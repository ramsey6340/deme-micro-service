package com.infinity.serviceactivity.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.infinity.serviceactivity.exceptions.InternalServerException;
import com.infinity.serviceactivity.exceptions.NotFoundException;
import com.infinity.serviceactivity.models.Post;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class PostService {
    public static final String COLLECTION_NAME = "posts";

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
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).orderBy("creationDate", Query.Direction.DESCENDING).get();
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

    public ResponseEntity<Post> patchPostInfo(String postId, Map<String, Object> postPatchInfo) {
        // Mise à jour des informations d'un post
        try {
            ResponseEntity<Post> responseEntity = getPostById(postId);
            if(responseEntity.getStatusCode() == HttpStatus.OK) {
                Post post = responseEntity.getBody();
                if (post != null){
                    post.setMessage((String) postPatchInfo.getOrDefault("message", post.getMessage()));
                    post.setVideoUrl((String) postPatchInfo.getOrDefault("videoUrl", post.getVideoUrl()));
                    post.setImageUrls((List<String>) postPatchInfo.getOrDefault("imageUrls", post.getImageUrls()));


                    // Enregistrement de la modification dans la base de données
                    return updatePost(post, postId);
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
                    .orderBy("creationDate", Query.Direction.DESCENDING)
                    .whereEqualTo("activityId", activityId).get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            return documents.stream().map(document -> document.toObject(Post.class)).toList();

        }catch (Exception e){
            throw new InternalServerException(e.getMessage());
        }
    }
}
