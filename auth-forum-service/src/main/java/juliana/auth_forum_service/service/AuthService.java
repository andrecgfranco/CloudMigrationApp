package juliana.auth_forum_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import juliana.auth_forum_service.dto.LoginRequest;
import juliana.auth_forum_service.dto.RegisterRequest;
import userProto.User;

@Component
public class AuthService {

    @Autowired
    private UserGrpcClientService userGrpcClientService;

    public User register(RegisterRequest request) {
        return userGrpcClientService.createUser(
                request.getUsername(),
                request.getEmail()
        ).orElseThrow(() -> new RuntimeException("UserService unavailable"));
    }

    public User authenticate(LoginRequest request) {
        return userGrpcClientService.authenticateUser(
                request.getUsername(),
                request.getEmail()
        ).orElseThrow(() -> new RuntimeException("UserService unavailable"));
    }

    public boolean deleteUser(Long id) {
        return userGrpcClientService.deleteUser(id)
                .orElseThrow(() -> new RuntimeException("UserService unavailable"));
    }
}