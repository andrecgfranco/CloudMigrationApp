package juliana.auth_forum_service.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import juliana.auth_forum_service.dto.CreatePostRequest;
import juliana.auth_forum_service.dto.PostResponse;
import juliana.auth_forum_service.handlers.ResourceNotFoundException;
import juliana.auth_forum_service.service.ForumService;

@WebMvcTest(ForumController.class)
class ForumControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ForumService forumService;

    private PostResponse mockPostResponse;

    @BeforeEach
    void setUp() {
        mockPostResponse = new PostResponse(
                1L,
                "Test Title",
                "Test Content",
                1L,
                LocalDateTime.now(),
                null
        );
    }

    // ── POST /api/v1/forum/posts ──────────────────────────────────────────────

    @Test
    void createPost_shouldReturn201_whenSuccess() throws Exception {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Test Title");
        request.setContent("Test Content");
        request.setAuthorId(1L);

        when(forumService.createPost(any())).thenReturn(mockPostResponse);

        mockMvc.perform(post("/api/v1/forum/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.content").value("Test Content"))
                .andExpect(jsonPath("$.authorId").value(1));
    }

    @Test
    void createPost_shouldReturn400_whenBodyIsEmpty() throws Exception {
        mockMvc.perform(post("/api/v1/forum/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPost_shouldReturn404_whenUserNotFound() throws Exception {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Test Title");
        request.setContent("Test Content");
        request.setAuthorId(99L);

        when(forumService.createPost(any()))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(post("/api/v1/forum/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());    // ← 404
    }

    // ── GET /api/v1/forum/posts ───────────────────────────────────────────────

    @Test
    void getAllPosts_shouldReturn200_whenPostsExist() throws Exception {
        when(forumService.getAllPosts(1L)).thenReturn(List.of(mockPostResponse));

        mockMvc.perform(get("/api/v1/forum/posts")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Title"))
                .andExpect(jsonPath("$[0].content").value("Test Content"));
    }

    @Test
    void getAllPosts_shouldReturn404_whenNoPosts() throws Exception {
        when(forumService.getAllPosts(1L))
                .thenThrow(new ResourceNotFoundException("No posts found"));

        mockMvc.perform(get("/api/v1/forum/posts")
                .param("userId", "1"))
                .andExpect(status().isNotFound());    // ← 404
    }

    // ── GET /api/v1/forum/posts/{postId} ──────────────────────────────────────

    @Test
    void getPostById_shouldReturn200_whenFound() throws Exception {
        when(forumService.getPostById(1L)).thenReturn(mockPostResponse);

        mockMvc.perform(get("/api/v1/forum/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.id").value(1));
    }

        @Test
        void getPostById_shouldReturn404_whenNotFound() throws Exception {
            when(forumService.getPostById(99L))
                    .thenThrow(new ResourceNotFoundException("Post not found"));

            mockMvc.perform(get("/api/v1/forum/posts/99"))
                    .andExpect(status().isNotFound());    // ← 404
        }
}
