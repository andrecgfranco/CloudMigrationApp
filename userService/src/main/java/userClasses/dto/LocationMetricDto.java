package userClasses.dto;

public class LocationMetricDto {

    private String locationName;
    private String metricName;
    private double value;

    public LocationMetricDto(String locationName, String metricName, double value) {
        this.locationName = locationName;
        this.metricName = metricName;
        this.value = value;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
    
}
