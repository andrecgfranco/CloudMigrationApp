package searchClasses.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LocationSummaryDTO {
    private long id;
    private String name;

    @JsonProperty("country_code")
    private String countryCode;

    @JsonProperty("metric_value")
    private Double metricValue;

    public LocationSummaryDTO() {
    }

    public LocationSummaryDTO(long id, String name, String countryCode, Double metricValue) {
        this.id = id;
        this.name = name;
        this.countryCode = countryCode;
        this.metricValue = metricValue;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Double getMetricValue() {
        return metricValue;
    }

    public void setMetricValue(Double metricValue) {
        this.metricValue = metricValue;
    }
}
