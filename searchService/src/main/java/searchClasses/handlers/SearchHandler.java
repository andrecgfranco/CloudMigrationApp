package searchClasses.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import searchClasses.repositories.LocationMetricsRepository;
import searchClasses.repositories.LocationRepository;
import searchClasses.repositories.MetricRepository;
import searchClasses.dto.IndicatorDTO;
import searchClasses.dto.LocationSummaryDTO;
import searchClasses.dto.TrendResultDTO;
import searchClasses.entities.LocationMetrics;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SearchHandler {
    
    @Autowired
    private LocationMetricsRepository locationMetricsRepository;
    
    @Autowired
    private LocationRepository locationRepository;
    
    @Autowired
    private MetricRepository metricRepository;

    /**
     * UC1 – Filtered Search of Locations
     * Search for locations with a metric within a value range
     */
    public List<LocationSummaryDTO> filteredSearchLocations(String dataset, Double minValue, Double maxValue) {
        if (dataset == null || dataset.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dataset parameter is required");
        }
        
        if (minValue != null && maxValue != null && minValue > maxValue) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "min_value cannot be greater than max_value");
        }
        
        // Set defaults if not provided
        if (minValue == null) {
            minValue = Double.NEGATIVE_INFINITY;
        }
        if (maxValue == null) {
            maxValue = Double.POSITIVE_INFINITY;
        }
        
        try {
            List<LocationMetrics> results = locationMetricsRepository.findLocationsWithinRange(dataset, minValue, maxValue);
            
            return results.stream()
                    .map(lm -> new LocationSummaryDTO(
                            lm.getLocation().getId(),
                            lm.getLocation().getName(),
                            lm.getLocation().getCode(),
                            lm.getValue()
                    ))
                    .distinct()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dataset not available or invalid parameters");
        }
    }
    
    /**
         * UC3 – Historical Evolution of Metrics
     * Get historical evolution of metrics for a specific location
     */
    public List<IndicatorDTO> compareLocationsHistory(long locationId, List<String> datasets, 
                                                       Integer startYear, Integer endYear) {
        if (!locationRepository.existsById(locationId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found");
        }
        
        if (datasets == null || datasets.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one dataset must be specified");
        }
        
        if (startYear == null) {
            startYear = 2015;
        }
        if (endYear == null) {
            endYear = Calendar.getInstance().get(Calendar.YEAR); //Might not need current year
        }
        
        if (startYear < 0 || endYear < 0 || startYear > endYear) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid year range");
        }
        
        try {
            List<LocationMetrics> metrics = locationMetricsRepository.findHistoryByLocationAndYearRange(
                    locationId,
                    startYear.shortValue(),
                    endYear.shortValue(),
                    datasets
            );
            
            return metrics.stream()
                    .map(lm -> new IndicatorDTO(
                            (int) lm.getYear(),
                            lm.getMetric().getName(),
                            lm.getValue(),
                            lm.getMetric().getUnit()
                    ))
                    .collect(Collectors.<IndicatorDTO>toList());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid parameters or dataset not available");
        }
    }

    /**
     * UC3 – Tendencies in Location Metrics
     * Find locations with highest increase or decrease in a metric over a period
     */
    public List<TrendResultDTO> getLocationTrends(String dataset, Integer years, Boolean isIncrease, Integer top) {
        if (dataset == null || dataset.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dataset parameter is required");
        }
        
        if (years == null || years <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Years parameter must be provided and greater than 0");
        }
        
        if (isIncrease == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "is_increase parameter is required");
        }
        
        if (top == null || top <= 0) {
            top = 10;
        }
        
        // Start and end years
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int endYear = currentYear;
        int startYear = endYear - years;
        
        try {
            // métricas delimitadas a anos específicos
            List<LocationMetrics> allMetrics = locationMetricsRepository.findTrendLocationsIncreasing(dataset, startYear, endYear, 10000);
            
            Map<Long, List<LocationMetrics>> byLocation = allMetrics.stream()
                .collect(Collectors.groupingBy(lm -> lm.getLocation().getId()));
            
            List<TrendResultDTO> trends = new ArrayList<>();
            
            for (Map.Entry<Long, List<LocationMetrics>> entry : byLocation.entrySet()) {
                List<LocationMetrics> locationData = entry.getValue();
                
                // Ano mínimo e máximo
                LocationMetrics startRecord = locationData.stream()
                    .min(Comparator.comparing(LocationMetrics::getYear))
                    .orElse(null);
                
                LocationMetrics endRecord = locationData.stream()
                    .max(Comparator.comparing(LocationMetrics::getYear))
                    .orElse(null);
                
                if (startRecord != null && endRecord != null) {
                    double startValue = startRecord.getValue();
                    double endValue = endRecord.getValue();
                    double change = endValue - startValue;
                    
                    LocationSummaryDTO locationDTO = new LocationSummaryDTO(
                        startRecord.getLocation().getId(),
                        startRecord.getLocation().getName(),
                        startRecord.getLocation().getCode(),
                        null
                    );
                    
                    trends.add(new TrendResultDTO(
                        locationDTO,
                        change,
                        startValue,
                        endValue,
                        dataset,
                        years
                    ));
                }
            }
            
            // IsIncrease flag para ordenar por aumento ou diminuição
            if (isIncrease) {
                trends.sort((a, b) -> Double.compare(b.getMetricChange(), a.getMetricChange())); // DESC for increase
            } else {
                trends.sort((a, b) -> Double.compare(a.getMetricChange(), b.getMetricChange())); // ASC for decrease
            }
            
            return trends.stream()
                .limit(top)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid parameters or dataset not available");
        }
    }
}
