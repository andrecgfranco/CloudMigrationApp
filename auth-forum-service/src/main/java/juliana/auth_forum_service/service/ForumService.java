package juliana.auth_forum_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import juliana.auth_forum_service.dto.CreatePostRequest;
import juliana.auth_forum_service.dto.PostResponse;
import juliana.auth_forum_service.entities.ForumPost;
import juliana.auth_forum_service.handlers.ResourceNotFoundException;
import juliana.auth_forum_service.repositories.ForumPostRepository;
import userProto.User;

@Component
public class ForumService {

    @Autowired
    private ForumPostRepository forumPostRepository;

    @Autowired
    private  UserGrpcClientService userGrpcClientService;


    public PostResponse createPost(CreatePostRequest request) {
        User author = userGrpcClientService.getUserById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("UserService unavailable"));

        if (author == null || author.getId() == 0) {
            throw new ResourceNotFoundException("User not found");
        }

        ForumPost post = new ForumPost(
                request.getTitle(),
                request.getContent(),
                request.getAuthorId()
        );

        ForumPost savedPost = forumPostRepository.save(post);
        return mapToResponse(savedPost);
    }

    public List<PostResponse> getAllPosts(Long userId) {
        List<ForumPost> posts;

        if (userId != null) {
            User user = userGrpcClientService.getUserById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("UserService unavailable"));

            if (user == null || user.getId() == 0) {
                throw new ResourceNotFoundException("User not found");
            }

            posts = forumPostRepository.findByAuthorIdOrderByCreatedAtDesc(userId);
        } else {
            posts = forumPostRepository.findAllByOrderByCreatedAtDesc();
        }

        if (posts.isEmpty()) {
            throw new ResourceNotFoundException(
                    userId != null ? "No posts found for user " + userId : "No posts found");
        }

        return posts.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public PostResponse getPostById(Long postId) {
        ForumPost post = forumPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        return mapToResponse(post);
    }

    private PostResponse mapToResponse(ForumPost post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthorId(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}