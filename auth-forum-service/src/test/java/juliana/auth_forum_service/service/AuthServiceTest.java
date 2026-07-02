package juliana.auth_forum_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import juliana.auth_forum_service.dto.LoginRequest;
import juliana.auth_forum_service.dto.RegisterRequest;
import userProto.User;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserGrpcClientService userGrpcClientService;

    @InjectMocks
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

    // ── register ─────────────────────────────────────────────────────────────

    @Test
    void register_shouldReturnUser_whenUserServiceAvailable() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("juliana");
        request.setEmail("juliana@test.com");

        when(userGrpcClientService.createUser("juliana", "juliana@test.com"))
                .thenReturn(Optional.of(mockUser));

        User result = authService.register(request);

        assertNotNull(result);
        assertEquals("juliana", result.getUserName());
        assertEquals("juliana@test.com", result.getEmail());
        verify(userGrpcClientService, times(1)).createUser("juliana", "juliana@test.com");
    }

    @Test
    void register_shouldThrowException_whenUserServiceUnavailable() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("juliana");
        request.setEmail("juliana@test.com");

        when(userGrpcClientService.createUser("juliana", "juliana@test.com"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register(request));

        assertEquals("UserService unavailable", exception.getMessage());
    }

    // ── authenticate ─────────────────────────────────────────────────────────

    @Test
    void authenticate_shouldReturnUser_whenCredentialsValid() {
        LoginRequest request = new LoginRequest();
        request.setUsername("juliana");
        request.setEmail("juliana@test.com");

        when(userGrpcClientService.authenticateUser("juliana", "juliana@test.com"))
                .thenReturn(Optional.of(mockUser));

        User result = authService.authenticate(request);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(userGrpcClientService, times(1)).authenticateUser("juliana", "juliana@test.com");
    }

    @Test
    void authenticate_shouldThrowException_whenUserServiceUnavailable() {
        LoginRequest request = new LoginRequest();
        request.setUsername("juliana");
        request.setEmail("juliana@test.com");

        when(userGrpcClientService.authenticateUser("juliana", "juliana@test.com"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.authenticate(request));

        assertEquals("UserService unavailable", exception.getMessage());
    }

    // ── deleteUser ───────────────────────────────────────────────────────────

    @Test
    void deleteUser_shouldReturnTrue_whenUserExists() {
        when(userGrpcClientService.deleteUser(1L))
                .thenReturn(Optional.of(true));

        boolean result = authService.deleteUser(1L);

        assertTrue(result);
        verify(userGrpcClientService, times(1)).deleteUser(1L);
    }

    @Test
    void deleteUser_shouldThrowException_whenUserServiceUnavailable() {
        when(userGrpcClientService.deleteUser(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.deleteUser(1L));

        assertEquals("UserService unavailable", exception.getMessage());
    }
}
