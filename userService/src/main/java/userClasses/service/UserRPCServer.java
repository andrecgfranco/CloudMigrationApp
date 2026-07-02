package userClasses.service;

import java.util.Optional;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import userClasses.repositories.UserRepository;
import userProto.RegisterUserRequest;



@GrpcService
public class UserRPCServer extends userProto.UserServiceGrpc.UserServiceImplBase {
    
    private final UserRepository userRepository;

    public UserRPCServer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void createUser (RegisterUserRequest request, StreamObserver<userProto.User> responseObserver) {
        String userName = request.getUserName();
        String email = request.getEmail();
        Optional<userClasses.entities.User> checkUser = userRepository.getUserByUsername(userName);
        if (checkUser.isPresent()) {
            responseObserver.onError(Status.ALREADY_EXISTS
                .withDescription("User already exists")
                .asRuntimeException());
            return;
        }
        checkUser = userRepository.getUserByEmail(email);
        if (checkUser.isPresent()) {
            responseObserver.onError(Status.ALREADY_EXISTS
                .withDescription("Email already in use")
                .asRuntimeException());
            return;
        }
        userClasses.entities.User user = new userClasses.entities.User(userName);
        user.setEmail(email);
         user = userRepository.save(user);

        userProto.User response = userProto.User.newBuilder()
            .setId(user.getId()).setUserName(user.getUsername()).setEmail(user.getEmail()).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }


    @Override
    public void deleteUser (userProto.UserDeleteRequest request, StreamObserver<userProto.UserDeleteResponse> responseObserver) {
        long userId = request.getId();
        Optional<userClasses.entities.User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            userProto.UserDeleteResponse response = userProto.UserDeleteResponse.newBuilder().setSuccess(false).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }
        userRepository.deleteById(userId);
        userProto.UserDeleteResponse response = userProto.UserDeleteResponse.newBuilder().setSuccess(true).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    @Override
    public void authenticateUser (userProto.RegisterUserRequest request, StreamObserver<userProto.User> responseObserver) {
        String userName = request.getUserName();
        String email = request.getEmail();
        Optional<userClasses.entities.User> userOpt = userRepository.getUserByUsername(userName);
        if (userOpt.isEmpty() || !userOpt.get().getEmail().equals(email)) {
            responseObserver.onError(Status.UNAUTHENTICATED
                .withDescription("Invalid username or email")
                .asRuntimeException());
            return;
        }
        userClasses.entities.User user = userOpt.get();
        userProto.User response = userProto.User.newBuilder()
            .setId(user.getId()).setUserName(user.getUsername()).setEmail(user.getEmail()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    @Override
    public void getUserById (userProto.GetUserByIdRequest request, StreamObserver<userProto.User> responseObserver) {
        long userId = request.getId();
        Optional<userClasses.entities.User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            responseObserver.onError(Status.NOT_FOUND
                .withDescription("User not found")
                .asRuntimeException());
            return;
        }
        userClasses.entities.User user = userOpt.get();
        userProto.User response = userProto.User.newBuilder()
            .setId(user.getId()).setUserName(user.getUsername()).setEmail(user.getEmail()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
