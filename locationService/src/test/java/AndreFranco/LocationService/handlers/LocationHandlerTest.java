package AndreFranco.LocationService.handlers;

import AndreFranco.LocationService.dto.LocationMetricsDto;
import AndreFranco.LocationService.dto.LocationSummaryDto;
import AndreFranco.LocationService.dto.TopEntryDto;
import AndreFranco.LocationService.entities.Location;
import AndreFranco.LocationService.entities.Metric;
import AndreFranco.LocationService.entities.Statistics;
import AndreFranco.LocationService.repositories.LocationRepository;
import AndreFranco.LocationService.repositories.StatisticRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class LocationHandlerTest {

    @InjectMocks
    private LocationHandler handler;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private StatisticRepository statisticRepository;

    private Location portugal;
    private Location spain;
    private Metric pli;
    private Statistics ptStat;

    @BeforeEach
    void setUp() {
        portugal = new Location();
        portugal.setId(1L);
        portugal.setName("Portugal");
        portugal.setCode("PT");

        spain = new Location();
        spain.setId(2L);
        spain.setName("Spain");
        spain.setCode("ES");

        pli = new Metric();
        pli.setId(1L);
        pli.setName("pli");
        pli.setLabel("Price Level Index");
        pli.setUnit("index");

        ptStat = new Statistics();
        ptStat.setId(1L);
        ptStat.setLocation(portugal);
        ptStat.setMetric(pli);
        ptStat.setValue(72.5);
        ptStat.setYear(2023);
    }

    @Test
    void listLocations_returnsAllLocations() {
        when(locationRepository.findAll()).thenReturn(List.of(portugal, spain));

        List<LocationSummaryDto> result = handler.listLocations();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Portugal");
        assertThat(result.get(1).getCountryCode()).isEqualTo("ES");
    }

    @Test
    void listLocations_emptyDatabase_returnsEmptyList() {
        when(locationRepository.findAll()).thenReturn(List.of());

        assertThat(handler.listLocations()).isEmpty();
    }

    @Test
    void getLocationSummary_existingId_returnsDto() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(portugal));

        LocationSummaryDto result = handler.getLocationSummary(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCountryCode()).isEqualTo("PT");
    }

    @Test
    void getLocationSummary_missingId_returnsNull() {
        when(locationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(handler.getLocationSummary(99L)).isNull();
    }

    @Test
    void getLocationById_withoutYear_returnsAllStats() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(portugal));
        when(statisticRepository.findByLocation_Id(1L)).thenReturn(List.of(ptStat));

        LocationMetricsDto result = handler.getLocationById(1L, null);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Portugal");
        assertThat(result.getMetrics()).hasSize(1);
        assertThat(result.getMetrics().get(0).getValue()).isEqualTo(72.5);
    }

    @Test
    void getLocationById_withYear_usesYearFilter() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(portugal));
        when(statisticRepository.findByLocation_IdAndYear(1L, 2023)).thenReturn(List.of(ptStat));

        LocationMetricsDto result = handler.getLocationById(1L, 2023);

        assertThat(result).isNotNull();
        assertThat(result.getMetrics()).hasSize(1);
        assertThat(result.getMetrics().get(0).getYear()).isEqualTo(2023);
    }

    @Test
    void getLocationById_missingId_returnsNull() {
        when(locationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(handler.getLocationById(99L, null)).isNull();
    }

    @Test
    void getLocationByCode_existingCode_returnsDto() {
        when(locationRepository.findByCode("PT")).thenReturn(Optional.of(portugal));
        when(statisticRepository.findByLocation_Code("PT")).thenReturn(List.of(ptStat));

        LocationMetricsDto result = handler.getLocationByCode("PT", null);

        assertThat(result).isNotNull();
        assertThat(result.getCountryCode()).isEqualTo("PT");
        assertThat(result.getMetrics()).hasSize(1);
    }

    @Test
    void getLocationByCode_missingCode_returnsNull() {
        when(locationRepository.findByCode("XX")).thenReturn(Optional.empty());

        assertThat(handler.getLocationByCode("XX", null)).isNull();
    }

    @Test
    void compareLocations_allIdsFound_returnsList() {
        when(locationRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(portugal, spain));
        when(statisticRepository.findLatestByLocationId(1L)).thenReturn(List.of(ptStat));
        when(statisticRepository.findLatestByLocationId(2L)).thenReturn(List.of());

        List<LocationMetricsDto> result = handler.compareLocations(List.of(1L, 2L), null);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCountryCode()).isEqualTo("PT");
        assertThat(result.get(1).getCountryCode()).isEqualTo("ES");
    }

    @Test
    void compareLocations_missingId_returnsNull() {
        when(locationRepository.findAllById(List.of(1L, 99L))).thenReturn(List.of(portugal));

        assertThat(handler.compareLocations(List.of(1L, 99L), null)).isNull();
    }

    @Test
    void getTopLocations_ascOrder_returnsRankedInOrder() {
        Statistics esStat = new Statistics();
        esStat.setId(2L);
        esStat.setLocation(spain);
        esStat.setMetric(pli);
        esStat.setValue(85.3);
        esStat.setYear(2023);

        when(statisticRepository.findLatestByMetricNameOrderByValueAsc("pli"))
                .thenReturn(List.of(ptStat, esStat));

        List<TopEntryDto> result = handler.getTopLocations("pli", "asc", 2);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getRank()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("Portugal");
        assertThat(result.get(0).getValue()).isEqualTo(72.5);
        assertThat(result.get(1).getRank()).isEqualTo(2);
    }

    @Test
    void getTopLocations_limitCutsResult() {
        Statistics esStat = new Statistics();
        esStat.setId(2L);
        esStat.setLocation(spain);
        esStat.setMetric(pli);
        esStat.setValue(85.3);
        esStat.setYear(2023);

        when(statisticRepository.findLatestByMetricNameOrderByValueAsc("pli"))
                .thenReturn(List.of(ptStat, esStat));

        List<TopEntryDto> result = handler.getTopLocations("pli", "asc", 1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Portugal");
    }
}
