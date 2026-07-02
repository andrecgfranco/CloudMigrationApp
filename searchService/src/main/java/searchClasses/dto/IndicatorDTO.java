package searchClasses.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IndicatorDTO {

    private Integer year;

    @JsonProperty("metric_name")
    private String metricName;

    @JsonProperty("metric_value")
    private Double metricValue;

    @JsonProperty("metric_unit")
    private String metricUnit;

    public IndicatorDTO() {
    }

    public IndicatorDTO(Integer year, String metricName, Double metricValue, String metricUnit) {
        this.year = year;
        this.metricName = metricName;
        this.metricValue = metricValue;
        this.metricUnit = metricUnit;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public Double getMetricValue() {
        return metricValue;
    }

    public void setMetricValue(Double metricValue) {
        this.metricValue = metricValue;
    }

    public String getMetricUnit() {
        return metricUnit;
    }

    public void setMetricUnit(String metricUnit) {
        this.metricUnit = metricUnit;
    }
}
