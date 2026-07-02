package juliana.auth_forum_service.service;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import juliana.auth_forum_service.handlers.DuplicateResourceException;
import juliana.auth_forum_service.handlers.ResourceNotFoundException;
import userProto.GetUserByIdRequest;
import userProto.RegisterUserRequest;
import userProto.User;
import userProto.UserDeleteRequest;
import userProto.UserDeleteResponse;
import userProto.UserServiceGrpc;

@Service
public class UserGrpcClientService {
    private static final Logger logger = LoggerFactory.getLogger(UserGrpcClientService.class);
    private static final long INITIAL_BACKOFF_MS = 100;
    private static final long MAX_BACKOFF_MS = 30000;
    private static final int MAX_RETRY_ATTEMPTS = 5;

    @Value("${grpc.client.userService.address:static://userService:50053}")
    private String userServiceAddress;

    private final AtomicReference<ManagedChannel> channelRef = new AtomicReference<>(null);
    private final AtomicBoolean userServiceAvailable = new AtomicBoolean(false);

    private ManagedChannel getOrCreateChannel() {
        if (channelRef.get() == null) {
            String[] parts = userServiceAddress.replace("static://", "").split(":");
            String host = parts[0];
            int port = Integer.parseInt(parts[1]);

            logger.info("Creating gRPC channel to {}:{}", host, port);
            ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
            channelRef.set(channel);
        }
        return channelRef.get();
    }

    private UserServiceGrpc.UserServiceBlockingStub getStub() {
        return UserServiceGrpc.newBlockingStub(getOrCreateChannel());
    }

    private <T> Optional<T> executeWithRetry(Supplier<T> operation, String operationName) {
        long backoffMs = INITIAL_BACKOFF_MS;
        long attempt = 1;

        while (true) {
            try {
                T result = operation.get();
                userServiceAvailable.set(true);
                if (attempt > 1) {
                    logger.info("✓ {} succeeded on attempt {}", operationName, attempt);
                }
                return Optional.of(result);
            } catch (StatusRuntimeException e) {
                userServiceAvailable.set(false);

                if (!isRetryable(e) || attempt >= MAX_RETRY_ATTEMPTS) {
                    logger.error("{} failed with non-retryable/terminal gRPC status {}: {}",
                        operationName,
                        e.getStatus().getCode(),
                        e.getStatus().getDescription());
                    throw mapGrpcException(e);
                }

                logger.warn("Attempt {} - {} failed: {} - retrying in {}ms", attempt, operationName,
                    e.getStatus().getCode(), backoffMs);

                try {
                    Thread.sleep(backoffMs);
                    backoffMs = Math.min(backoffMs * 2, MAX_BACKOFF_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    logger.error("Retry loop interrupted for {}", operationName);
                    return Optional.empty();
                }
                attempt++;
            } catch (Exception e) {
                logger.error("{} failed unexpectedly: {}", operationName,
                    e.getClass().getSimpleName() + ": " + e.getMessage());
                userServiceAvailable.set(false);
                return Optional.empty();
            }
        }
    }

    private boolean isRetryable(StatusRuntimeException e) {
        Status.Code code = e.getStatus().getCode();
        return code == Status.Code.UNAVAILABLE
            || code == Status.Code.DEADLINE_EXCEEDED
            || code == Status.Code.RESOURCE_EXHAUSTED;
    }

    private RuntimeException mapGrpcException(StatusRuntimeException e) {
        Status.Code code = e.getStatus().getCode();
        String description = e.getStatus().getDescription();
        String message = (description == null || description.isBlank())
            ? "User service request failed"
            : description;

        if (code == Status.Code.ALREADY_EXISTS) {
            return new DuplicateResourceException(message);
        }
        if (code == Status.Code.NOT_FOUND) {
            return new ResourceNotFoundException(message);
        }
        if (code == Status.Code.UNAUTHENTICATED || code == Status.Code.PERMISSION_DENIED) {
            return new RuntimeException(message);
        }

        return new RuntimeException("User service error (" + code + "): " + message);
    }

    public Optional<User> createUser(String username, String email) {
        return executeWithRetry(() -> {
            RegisterUserRequest request = RegisterUserRequest.newBuilder()
                    .setUserName(username)
                    .setEmail(email)
                    .build();
            return getStub().createUser(request);
        }, "createUser");
    }

    public Optional<User> authenticateUser(String username, String email) {
        return executeWithRetry(() -> {
            RegisterUserRequest request = RegisterUserRequest.newBuilder()
                    .setUserName(username)
                    .setEmail(email)
                    .build();
            return getStub().authenticateUser(request);
        }, "authenticateUser");
    }

    public Optional<Boolean> deleteUser(Long id) {
        return executeWithRetry(() -> {
            UserDeleteRequest request = UserDeleteRequest.newBuilder()
                    .setId(id)
                    .build();
            UserDeleteResponse response = getStub().deleteUser(request);
            return response.getSuccess();
        }, "deleteUser");
    }

    public Optional<User> getUserById(Long id) {
        return executeWithRetry(() -> {
            GetUserByIdRequest request = GetUserByIdRequest.newBuilder()
                    .setId(id)
                    .build();
            return getStub().getUserById(request);
        }, "getUserById");
    }

    public boolean isUserServiceAvailable() {
        return userServiceAvailable.get();
    }
}
