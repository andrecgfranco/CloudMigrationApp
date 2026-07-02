package AndreFranco.LocationService.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import AndreFranco.LocationService.dto.LocationMetricsDto;
import AndreFranco.LocationService.dto.LocationSummaryDto;
import AndreFranco.LocationService.dto.TopEntryDto;
import AndreFranco.LocationService.handlers.LocationHandler;

@Component
public class LocationController {

    private static final List<String> VALID_METRICS = List.of(
            "pli", "hpi", "crime", "air_quality", "transport",
            "health", "unemployment", "working_hours", "life_expectancy");

    // Limites pra UC4
    private static final int DEFAULT_LIMIT = 5;
    private static final int MAX_LIMIT = 15;
    private static final int MIN_LIMIT = 1;

    // Limites para o parâmetro year (UC2)
    private static final int MIN_YEAR = 1970;
    private static final int MAX_YEAR = 2026;

    @Autowired
    private LocationHandler handler;

    // -------------------------------------------------------------------------
    // UC1 – Listar todas as localidades
    // -------------------------------------------------------------------------

    public ResponseEntity<?> listLocations() {
        List<LocationSummaryDto> result = handler.listLocations();
        return ResponseEntity.ok(result);
    }

    // -------------------------------------------------------------------------
    // UC2 – Detalhes por ID
    // -------------------------------------------------------------------------

    public ResponseEntity<?> getLocationById(Long id, Integer year) {
        if (year != null && (year < MIN_YEAR || year > MAX_YEAR))
            return ResponseEntity.status(400).body("Invalid year");

        // handler retorna null em vez de Optional.empty() após refactor para @Cacheable
        LocationMetricsDto result = handler.getLocationById(id, year);
        if (result == null)
            return ResponseEntity.status(404).body("Location not found");
        return ResponseEntity.ok(result);
    }

    // UC2 – Detalhes por código ISO

    public ResponseEntity<?> getLocationByCode(String code, Integer year) {
        if (code.length() != 2)
            return ResponseEntity.status(400).body("Invalid ISO code");

        if (year != null && (year < MIN_YEAR || year > MAX_YEAR))
            return ResponseEntity.status(400).body("Invalid year");

        // handler retorna null em vez de Optional.empty() após refactor para @Cacheable
        LocationMetricsDto result = handler.getLocationByCode(code, year);
        if (result == null)
            return ResponseEntity.status(404).body("Location not found");
        return ResponseEntity.ok(result);
    }

    // -------------------------------------------------------------------------
    // UC3 – Comparação entre localidades
    // -------------------------------------------------------------------------

    public ResponseEntity<?> compareLocations(List<Long> ids, List<String> metrics) {
        if (ids == null || ids.size() < 2) {
            return ResponseEntity.status(400).body("At least 2 location ids are required");
        }
        if (metrics != null && !metrics.isEmpty()) {
            List<String> invalid = metrics.stream()
                    .filter(m -> !VALID_METRICS.contains(m))
                    .toList();
            if (!invalid.isEmpty()) {
                return ResponseEntity.status(400).body("Invalid metrics: " + invalid);
            }
        }

        // handler retorna null em vez de Optional.empty() após refactor para @Cacheable
        List<LocationMetricsDto> result = handler.compareLocations(ids, metrics);
        if (result == null)
            return ResponseEntity.status(404).body("One or more locations not found");

        return ResponseEntity.ok(result);
    }

    // -------------------------------------------------------------------------
    // UC4 – Top por métrica
    // -------------------------------------------------------------------------

    public ResponseEntity<?> getTopLocations(String metric, String order, Integer limit) {
        if (metric == null || !VALID_METRICS.contains(metric)) {
            return ResponseEntity.status(400).body("Invalid or missing metric");
        }
        if (order != null && !order.equals("asc") && !order.equals("desc")) {
            return ResponseEntity.status(400).body("Order must be 'asc' or 'desc'");
        }

        int resolvedLimit = (limit != null) ? limit : DEFAULT_LIMIT;
        if (resolvedLimit < MIN_LIMIT || resolvedLimit > MAX_LIMIT) {
            return ResponseEntity.status(400).body("Limit must be between 1 and 15");
        }

        String resolvedOrder = (order != null) ? order : "asc";
        List<TopEntryDto> result = handler.getTopLocations(metric, resolvedOrder, resolvedLimit);
        return ResponseEntity.ok(result);
    }
}