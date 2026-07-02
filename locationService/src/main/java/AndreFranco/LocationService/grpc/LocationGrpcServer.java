package AndreFranco.LocationService.grpc;

import AndreFranco.LocationService.dto.LocationSummaryDto;
import AndreFranco.LocationService.handlers.LocationHandler;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import userProto.*;
import userProto.LocationServiceGrpc;
import com.google.protobuf.Empty;

import java.util.List;

@GrpcService
public class LocationGrpcServer extends LocationServiceGrpc.LocationServiceImplBase {

    private final LocationHandler handler;

    public LocationGrpcServer(LocationHandler handler) {
        this.handler = handler;
    }

    @Override
    public void getLocation(GetLocationRequest request, StreamObserver<userProto.Location> responseObserver) {

        LocationSummaryDto dto = handler.getLocationSummary(request.getId());

        if (dto == null) {
            responseObserver.onError(
                io.grpc.Status.NOT_FOUND
                    .withDescription("Location not found")
                    .asRuntimeException()
            );
            return;
        }

        userProto.Location response = userProto.Location.newBuilder()
                .setId(dto.getId())
                .setName(dto.getName())
                .setCode(dto.getCountryCode())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getAllLocations(Empty request, StreamObserver<userProto.Location> responseObserver) {

        List<LocationSummaryDto> locations = handler.listLocations();

        for (LocationSummaryDto dto : locations) {
            userProto.Location response = userProto.Location.newBuilder()
                    .setId(dto.getId())
                    .setName(dto.getName())
                    .setCode(dto.getCountryCode())
                    .build();

            responseObserver.onNext(response);
        }

        responseObserver.onCompleted();
    }
}