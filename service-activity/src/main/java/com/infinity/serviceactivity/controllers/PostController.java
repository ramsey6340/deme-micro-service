package com.infinity.serviceactivity.controllers;

import com.infinity.serviceactivity.models.Post;
import com.infinity.serviceactivity.services.PostService;
import io.swagger.v3.oas.annotations.Operation;
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
    @PatchMapping(value = "{postId}")
    public ResponseEntity<Post> patchPostInfo(@PathVariable String postId, @RequestBody Map<String, Object> postPatchInfo) throws ExecutionException, InterruptedException {
        return postService.patchPostInfo(postId, postPatchInfo);
    }
}
