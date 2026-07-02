package juliana.auth_forum_service.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import juliana.auth_forum_service.dto.LoginRequest;
import juliana.auth_forum_service.dto.RegisterRequest;
import juliana.auth_forum_service.service.AuthService;
import userProto.User;

@WebMvcTest(
    controllers = AuthController.class,
    excludeAutoConfiguration = {
        net.devh.boot.grpc.client.autoconfigure.GrpcClientAutoConfiguration.class,
        net.devh.boot.grpc.server.autoconfigure.GrpcServerAutoConfiguration.class,
        net.devh.boot.grpc.server.autoconfigure.GrpcServerFactoryAutoConfiguration.class
    }
)
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.newBuilder()
                .setId(1)
                .setUserName("juliana")
                .setEmail("juliana@test.com")
                .build();
    }

    // ── POST /api/v1/auth/register ────────────────────────────────────────────

    @Test
    void register_shouldReturn201_whenSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("juliana");
        request.setEmail("juliana@test.com");

        when(authService.register(any())).thenReturn(mockUser);

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userNName").value("juliana"))
                .andExpect(jsonPath("$.email").value("juliana@test.com"));
    }

    @Test
    void register_shouldReturn400_whenUserServiceFails() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("juliana");
        request.setEmail("juliana@test.com");

        when(authService.register(any())).thenThrow(new RuntimeException("UserService unavailable"));

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_shouldReturn400_whenBodyIsEmpty() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    // ── POST /api/v1/auth/login ───────────────────────────────────────────────

    @Test
    void login_shouldReturn200_whenSuccess() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("juliana");
        request.setEmail("juliana@test.com");

        when(authService.authenticate(any())).thenReturn(mockUser);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userNName").value("juliana"))
                .andExpect(jsonPath("$.email").value("juliana@test.com"));
    }

    @Test
    void login_shouldReturn401_whenAuthenticationFails() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("juliana");
        request.setEmail("wrong@test.com");

        when(authService.authenticate(any())).thenThrow(new RuntimeException("Invalid credentials"));

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    // ── DELETE /api/v1/auth/{id} ──────────────────────────────────────────────

    @Test
    void deleteUser_shouldReturn200_whenSuccess() throws Exception {
        when(authService.deleteUser(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/auth/delete/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));
    }

    @Test
    void deleteUser_shouldReturn400_whenUserServiceFails() throws Exception {
        when(authService.deleteUser(1L)).thenThrow(new RuntimeException("UserService unavailable"));

        mockMvc.perform(delete("/api/v1/auth/delete/1"))
                .andExpect(status().isBadRequest());
    }
}
