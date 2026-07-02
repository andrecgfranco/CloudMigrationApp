package juliana.auth_forum_service.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import juliana.auth_forum_service.dto.CreatePostRequest;
import juliana.auth_forum_service.dto.PostResponse;
import juliana.auth_forum_service.service.ForumService;

@RestController
@RequestMapping("/api/v1/forum/posts")
public class ForumController {

    private final ForumService forumService;

    public ForumController(ForumService forumService) {
        this.forumService = forumService;
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts(@RequestParam (name = "userId", required = false) Long userId) {
        return ResponseEntity.ok(forumService.getAllPosts(userId));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long postId) {
        return ResponseEntity.ok(forumService.getPostById(postId));
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody CreatePostRequest request) {
        PostResponse response = forumService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}