package AndreFranco.LocationService.controllers;

import AndreFranco.LocationService.dto.LocationMetricsDto;
import AndreFranco.LocationService.dto.LocationSummaryDto;
import AndreFranco.LocationService.dto.TopEntryDto;
import AndreFranco.LocationService.handlers.LocationHandler;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class LocationControllerTest {

    @InjectMocks
    private LocationController controller;

    @Mock
    private LocationHandler handler;

    // ---- UC2: getLocationByCode validation ----

    @Test
    void getLocationByCode_tooShortCode_returns400() {
        ResponseEntity<?> response = controller.getLocationByCode("P", null);
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void getLocationByCode_tooLongCode_returns400() {
        ResponseEntity<?> response = controller.getLocationByCode("POR", null);
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void getLocationByCode_validCode_notFound_returns404() {
        when(handler.getLocationByCode("XX", null)).thenReturn(null);
        ResponseEntity<?> response = controller.getLocationByCode("XX", null);
        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void getLocationByCode_validCode_found_returns200() {
        LocationMetricsDto dto = new LocationMetricsDto(1L, "Portugal", "PT", List.of());
        when(handler.getLocationByCode("PT", null)).thenReturn(dto);
        ResponseEntity<?> response = controller.getLocationByCode("PT", null);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    // ---- UC1: listLocations ----

    @Test
    void listLocations_always_returns200() {
        when(handler.listLocations()).thenReturn(List.of(new LocationSummaryDto(1L, "Portugal", "PT")));
        ResponseEntity<?> response = controller.listLocations();
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    // ---- UC3: compareLocations validation ----

    @Test
    void compareLocations_nullIds_returns400() {
        ResponseEntity<?> response = controller.compareLocations(null, null);
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void compareLocations_singleId_returns400() {
        ResponseEntity<?> response = controller.compareLocations(List.of(1L), null);
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void compareLocations_invalidMetric_returns400() {
        ResponseEntity<?> response = controller.compareLocations(List.of(1L, 2L), List.of("invalid_metric"));
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void compareLocations_missingLocation_returns404() {
        when(handler.compareLocations(List.of(1L, 99L), null)).thenReturn(null);
        ResponseEntity<?> response = controller.compareLocations(List.of(1L, 99L), null);
        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }

    // ---- UC4: getTopLocations validation ----

    @Test
    void getTopLocations_nullMetric_returns400() {
        ResponseEntity<?> response = controller.getTopLocations(null, null, null);
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void getTopLocations_invalidMetric_returns400() {
        ResponseEntity<?> response = controller.getTopLocations("not_a_metric", null, null);
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void getTopLocations_invalidOrder_returns400() {
        ResponseEntity<?> response = controller.getTopLocations("pli", "random", null);
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void getTopLocations_limitTooHigh_returns400() {
        ResponseEntity<?> response = controller.getTopLocations("pli", "asc", 100);
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void getTopLocations_limitBelowMin_returns400() {
        ResponseEntity<?> response = controller.getTopLocations("pli", "asc", 0);
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void getTopLocations_validParams_returns200() {
        when(handler.getTopLocations("pli", "asc", 5)).thenReturn(List.of(new TopEntryDto(1, "Portugal", 72.5)));
        ResponseEntity<?> response = controller.getTopLocations("pli", "asc", 5);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }
}
