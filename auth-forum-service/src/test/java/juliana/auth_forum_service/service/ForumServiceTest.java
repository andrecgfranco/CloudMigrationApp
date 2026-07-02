package juliana.auth_forum_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import juliana.auth_forum_service.dto.CreatePostRequest;
import juliana.auth_forum_service.dto.PostResponse;
import juliana.auth_forum_service.entities.ForumPost;
import juliana.auth_forum_service.handlers.ResourceNotFoundException;
import juliana.auth_forum_service.repositories.ForumPostRepository;
import userProto.User;

@ExtendWith(MockitoExtension.class)
class ForumServiceTest {

    @Mock
    private ForumPostRepository forumPostRepository;

    @Mock
    private UserGrpcClientService userGrpcClientService;

    @InjectMocks
    private ForumService forumService;

    private User mockUser;
    private ForumPost mockPost;

    @BeforeEach
    void setUp() {
        mockUser = User.newBuilder()
                .setId(1)
                .setUserName("juliana")
                .setEmail("juliana@test.com")
                .build();

        mockPost = new ForumPost("Test Title", "Test Content", 1L);
        
    }

    // ── createPost ───────────────────────────────────────────────────────────

    @Test
    void createPost_shouldReturnPostResponse_whenUserExists() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Test Title");
        request.setContent("Test Content");
        request.setAuthorId(1L);

        when(userGrpcClientService.getUserById(1L))
                .thenReturn(Optional.of(mockUser));
        when(forumPostRepository.save(any(ForumPost.class)))
                .thenReturn(mockPost);

        PostResponse result = forumService.createPost(request);

        assertNotNull(result);
        assertEquals("Test Title", result.getTitle());
        assertEquals("Test Content", result.getContent());
        assertEquals(1L, result.getAuthorId());
        verify(forumPostRepository, times(1)).save(any(ForumPost.class));
    }

    @Test
    void createPost_shouldThrowException_whenUserServiceUnavailable() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Test Title");
        request.setContent("Test Content");
        request.setAuthorId(1L);

        when(userGrpcClientService.getUserById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> forumService.createPost(request));

        verify(forumPostRepository, never()).save(any());
    }

    // ── getAllPosts ───────────────────────────────────────────────────────────

    @Test
    void getAllPosts_shouldReturnList_whenPostsExist() {
        when(userGrpcClientService.getUserById(1L))
                .thenReturn(Optional.of(mockUser));
        when(forumPostRepository.findByAuthorIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(mockPost));

        List<PostResponse> result = forumService.getAllPosts(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Title", result.get(0).getTitle());
    }

    @Test
    void getAllPosts_shouldThrowException_whenNoPosts() {
        when(userGrpcClientService.getUserById(1L))
                .thenReturn(Optional.of(mockUser));
        when(forumPostRepository.findByAuthorIdOrderByCreatedAtDesc(1L))
                .thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class,
                () -> forumService.getAllPosts(1L));
    }

    @Test
    void getAllPosts_shouldThrowException_whenUserServiceUnavailable() {
        when(userGrpcClientService.getUserById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> forumService.getAllPosts(1L));

        verify(forumPostRepository, never()).findByAuthorIdOrderByCreatedAtDesc(any());
    }

    @Test
    void getAllPosts_shouldReturnAllPosts_whenUserIdIsNull() {
        when(forumPostRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(List.of(mockPost));

        List<PostResponse> result = forumService.getAllPosts(null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userGrpcClientService, never()).getUserById(any());
    }

    @Test
    void getAllPosts_shouldThrowException_whenUserIdIsNullAndNoPosts() {
        when(forumPostRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class,
                () -> forumService.getAllPosts(null));
    }

    // ── getPostById ──────────────────────────────────────────────────────────

    @Test
    void getPostById_shouldReturnPost_whenExists() {
        when(forumPostRepository.findById(1L))
                .thenReturn(Optional.of(mockPost));

        PostResponse result = forumService.getPostById(1L);

        assertNotNull(result);
        assertEquals("Test Title", result.getTitle());
    }

    @Test
    void getPostById_shouldThrowException_whenNotFound() {
        when(forumPostRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> forumService.getPostById(99L));
    }
}
