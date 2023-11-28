package com.infinity.serviceactivity.controllers;

import com.infinity.serviceactivity.models.Campaign;
import com.infinity.serviceactivity.models.Post;
import com.infinity.serviceactivity.services.PostService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/activities/posts/")
public class PostController {

    @Autowired
    private PostService postService;


    @PostMapping(value = "organizations/{organizationId}", params = "activityId")
    @Operation(summary = "Ajouter un post")
    public ResponseEntity<String> createPost(@PathVariable String organizationId, @RequestParam String activityId, @Valid @RequestBody Post post) {
        return postService.createPost(organizationId, activityId, post);
    }

    @Operation(summary = "Récuperer une liste de post à partir d'une liste de cause")
    @GetMapping(value = "favorite-causes", params = "causes")
    public List<Post> getPostsByCauses(@RequestParam List<String> causes) {
        return postService.getPostsByCauses(causes);
    }

    @Operation(summary = "Récuperer une liste de post pour une activité précise")
    @GetMapping(value = "activities/{activityId}")
    public List<Post> getPostsByActivityId(@PathVariable String activityId) {
        return postService.getPostsByActivityId(activityId);
    }

    @Operation(summary = "Récuperer tous les posts")
    @GetMapping("")
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @Operation(summary = "Récuperer un post par son ID")
    @GetMapping("{postId}")
    public ResponseEntity<Post> getPostById(@PathVariable String postId) throws InterruptedException {
        return postService.getPostById(postId);
    }

    @Operation(summary = "Modifier les données d'une post")
    @PatchMapping(value = "organizations/{organizationId}/posts/{postId}")
    public ResponseEntity<Post> patchPostInfo(@PathVariable String organizationId, @PathVariable String postId, @RequestBody Map<String, Object> postPatchInfo) throws ExecutionException, InterruptedException {
        return postService.patchPostInfo(organizationId, postId, postPatchInfo);
    }

    @DeleteMapping("organizations/{organizationId}/posts/{postId}")
    @Operation(summary = "Supprimer un post")
    public ResponseEntity<String> deleteActivity(@PathVariable String organizationId, @PathVariable String postId) {
        return postService.deletePost(organizationId, postId);
    }

    @GetMapping(value = "organizations/{organizationId}/posts/")
    @Operation(summary = "Récupérer les posts d'une organisation")
    public List<Post> getAllPostsForOrganization(@PathVariable String organizationId) {
        return postService.getAllPostsForOrganization(organizationId);
    }
}
