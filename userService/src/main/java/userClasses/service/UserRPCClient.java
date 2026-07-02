package userClasses.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import io.grpc.StatusRuntimeException;
import userClasses.entities.Location;
import userClasses.entities.Metric;
import userClasses.dto.LocationMetricDto;
import userProto.GetLocationRequest;
import userProto.LocationServiceGrpc;
import userProto.MetricServiceGrpc;

@Service
public class UserRPCClient {
    private static final Logger logger = LoggerFactory.getLogger(UserRPCClient.class);
    private static final long INITIAL_BACKOFF_MS = 100;
    private static final long MAX_BACKOFF_MS = 30000;
    private static final int MAX_RETRY_ATTEMPTS = 5;

    @Value("${grpc.client.locationService.address:static://locations-service:50051}")
    private String locationServiceAddress;

    @Value("${grpc.client.metricService.address:static://searchService:50052}")
    private String metricServiceAddress;

    private final AtomicReference<ManagedChannel> locationChannelRef = new AtomicReference<>(null);
    private final AtomicReference<ManagedChannel> metricChannelRef = new AtomicReference<>(null);

    private final AtomicBoolean locationServiceAvailable = new AtomicBoolean(false);
    private final AtomicBoolean metricServiceAvailable = new AtomicBoolean(false);

    private ManagedChannel getOrCreateLocationChannel() {
        if (locationChannelRef.get() == null) {
            String[] parts = locationServiceAddress.replace("static://", "").split(":");
            String host = parts[0];
            int port = Integer.parseInt(parts[1]);

            logger.info("Creating gRPC channel to locationService at {}:{}", host, port);
            ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
            locationChannelRef.set(channel);
        }
        return locationChannelRef.get();
    }

    private ManagedChannel getOrCreateMetricChannel() {
        if (metricChannelRef.get() == null) {
            String[] parts = metricServiceAddress.replace("static://", "").split(":");
            String host = parts[0];
            int port = Integer.parseInt(parts[1]);

            logger.info("Creating gRPC channel to metricService at {}:{}", host, port);
            ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
            metricChannelRef.set(channel);
        }
        return metricChannelRef.get();
    }

    private LocationServiceGrpc.LocationServiceBlockingStub getLocationStub() {
        return LocationServiceGrpc.newBlockingStub(getOrCreateLocationChannel());
    }

    private MetricServiceGrpc.MetricServiceBlockingStub getMetricStub() {
        return MetricServiceGrpc.newBlockingStub(getOrCreateMetricChannel());
    }

    private boolean isRetryable(StatusRuntimeException e) {
        io.grpc.Status.Code code = e.getStatus().getCode();
        return code == io.grpc.Status.Code.UNAVAILABLE
            || code == io.grpc.Status.Code.DEADLINE_EXCEEDED
            || code == io.grpc.Status.Code.RESOURCE_EXHAUSTED;
    }

    private <T> Optional<T> executeWithRetry(Supplier<T> operation, String operationName, AtomicBoolean availability) {
        long backoffMs = INITIAL_BACKOFF_MS;
        long attempt = 1;

        while (true) {
            try {
                T result = operation.get();
                availability.set(true);
                if (attempt > 1) {
                    logger.info("✓ {} succeeded on attempt {}", operationName, attempt);
                }
                return Optional.of(result);
            } catch (StatusRuntimeException e) {
                availability.set(false);

                if (!isRetryable(e) || attempt >= MAX_RETRY_ATTEMPTS) {
                    logger.error("{} failed with non-retryable/terminal gRPC status {}: {}",
                        operationName,
                        e.getStatus().getCode(),
                        e.getStatus().getDescription());
                    return Optional.empty();
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
                availability.set(false);
                return Optional.empty();
            }
        }
    }

    public Optional<Location> getLocationById(long id) {
        return executeWithRetry(() -> {
            GetLocationRequest request = GetLocationRequest.newBuilder().setId(id).build();
            userProto.Location response = getLocationStub().getLocation(request);
            if (response == null) {
                return null;
            }
            Location value = new Location(response.getName(), response.getCode());
            value.setId(response.getId());
            return value;
        }, "getLocationById", locationServiceAvailable);
    }



    public Optional<Metric> getMetricByName(String name) {
        return executeWithRetry(() -> {
            userProto.GetMetricByNameRequest request = userProto.GetMetricByNameRequest.newBuilder().setName(name).build();
            userProto.Metric response = getMetricStub().getMetricByName(request);
            if (response == null) {
                return null;
            }
            Metric value = new Metric(response.getName(), response.getLabel(), response.getUnit());
            value.setId(response.getId());
            return value;
        }, "getMetricByName", metricServiceAvailable);
    }


    public Optional<List<LocationMetricDto>> getLocationMetricsOrderedByMetricId(long metricId) {
        return executeWithRetry(() -> {
            List<LocationMetricDto> locationMetrics = new ArrayList<>();
            Iterator<userProto.LocationMetric> responseStream = getMetricStub().getLocationMetricsOrderedByMetricId(
                userProto.GetLocationMetricsByMetricIdRequest.newBuilder().setMetricId(metricId).build());
            responseStream.forEachRemaining(response -> {
                LocationMetricDto locationMetric = new LocationMetricDto(response.getLocationName(), response.getMetricName(), response.getValue());
                locationMetrics.add(locationMetric);
            });
            return locationMetrics;
        }, "getLocationMetricsOrderedByMetricId", metricServiceAvailable);
    }
    

    public boolean isLocationServiceAvailable() {
        return locationServiceAvailable.get();
    }
    
     
    public boolean isMetricServiceAvailable() {
        return metricServiceAvailable.get();
    }
        
}
