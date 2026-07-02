package searchClasses.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import searchClasses.entities.LocationMetrics;
import searchClasses.repositories.LocationMetricsRepository;
import searchClasses.repositories.MetricRepository;
import userProto.GetMetricByNameRequest;
import userProto.GetLocationMetricsByMetricIdRequest;
import userProto.LocationMetric;


@GrpcService
public class SearchRPC extends userProto.MetricServiceGrpc.MetricServiceImplBase {

    @Autowired
    private MetricRepository metricRepository;

    @Autowired
    private LocationMetricsRepository locationMetricsRepository;


    public void getMetricByName(GetMetricByNameRequest request, StreamObserver<userProto.Metric> responseObserver) {
        String metricName = request.getName();
        Optional<searchClasses.entities.Metric> metricOpt = metricRepository.findByName(metricName);
        if (metricOpt.isPresent()) {
            searchClasses.entities.Metric metric = metricOpt.get();
            userProto.Metric response = userProto.Metric.newBuilder()
                .setId(metric.getId())
                .setName(metric.getName())
                .setUnit(metric.getUnit())
                .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new RuntimeException("Metric not found"));
        }
    }

    public void getLocationMetricsOrderedByMetricId(GetLocationMetricsByMetricIdRequest request, StreamObserver<LocationMetric> responseObserver) {
        long metricId = request.getMetricId();
        List<LocationMetrics> locationMetricsList = locationMetricsRepository.findByMetric_IdOrderByValueAsc(metricId);
        for (LocationMetrics lm : locationMetricsList) {
            LocationMetric response = LocationMetric.newBuilder()
                .setId(lm.getId())
                .setLocationName(lm.getLocation().getName())
                .setMetricName(lm.getMetric().getName())
                .setValue(lm.getValue())
                .build();
            responseObserver.onNext(response);
        }
        responseObserver.onCompleted();
    }

}


