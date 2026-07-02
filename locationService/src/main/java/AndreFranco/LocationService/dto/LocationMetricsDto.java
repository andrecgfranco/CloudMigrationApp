package AndreFranco.LocationService.dto;

import AndreFranco.LocationService.entities.Location;
import AndreFranco.LocationService.entities.Statistics;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class LocationMetricsDto {

    private Long id;
    private String name;

    @JsonProperty("country_code")
    private String countryCode;

    private List<MetricDto> metrics;

    public LocationMetricsDto() {}

    public LocationMetricsDto(Long id, String name, String countryCode, List<MetricDto> metrics) {
        this.id          = id;
        this.name        = name;
        this.countryCode = countryCode;
        this.metrics     = metrics;
    }

    public static LocationMetricsDto toDto(Location location, List<Statistics> statistics) {
        return new LocationMetricsDto(
                location.getId(),
                location.getName(),
                location.getCode(),
                statistics.stream().map(MetricDto::toDto).toList());
    }

    public Long getId()                             { return id; }
    public String getName()                         { return name; }
    public String getCountryCode()                  { return countryCode; }
    public List<MetricDto> getMetrics()             { return metrics; }

    public void setId(Long id)                      { this.id = id; }
    public void setName(String name)                { this.name = name; }
    public void setCountryCode(String code)         { this.countryCode = code; }
    public void setMetrics(List<MetricDto> metrics) { this.metrics = metrics; }
}