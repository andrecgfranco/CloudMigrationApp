package searchClasses.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TrendResultDTO {

    private LocationSummaryDTO location;

    @JsonProperty("metric_change")
    private Double metricChange;

    @JsonProperty("start_value")
    private Double startValue;

    @JsonProperty("end_value")
    private Double endValue;

    private String metric;
    private Integer years;

    public TrendResultDTO() {
    }

    public TrendResultDTO(LocationSummaryDTO location, Double metricChange, Double startValue, Double endValue,
                          String metric, Integer years) {
        this.location = location;
        this.metricChange = metricChange;
        this.startValue = startValue;
        this.endValue = endValue;
        this.metric = metric;
        this.years = years;
    }

    public LocationSummaryDTO getLocation() {
        return location;
    }

    public void setLocation(LocationSummaryDTO location) {
        this.location = location;
    }

    public Double getMetricChange() {
        return metricChange;
    }

    public void setMetricChange(Double metricChange) {
        this.metricChange = metricChange;
    }

    public Double getStartValue() {
        return startValue;
    }

    public void setStartValue(Double startValue) {
        this.startValue = startValue;
    }

    public Double getEndValue() {
        return endValue;
    }

    public void setEndValue(Double endValue) {
        this.endValue = endValue;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public Integer getYears() {
        return years;
    }

    public void setYears(Integer years) {
        this.years = years;
    }
}
