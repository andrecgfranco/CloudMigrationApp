package AndreFranco.LocationService.dto;

import AndreFranco.LocationService.entities.Statistics;

public class MetricDto {

    private String name;
    private String label;
    private double value;
    private String unit;
    private int year;

    public MetricDto() {}

    public MetricDto(String name, String label, double value, String unit, int year) {
        this.name  = name;
        this.label = label;
        this.value = value;
        this.unit  = unit;
        this.year  = year;
    }

    public static MetricDto toDto(Statistics statistics) {
        return new MetricDto(
                statistics.getMetric().getName(),
                statistics.getMetric().getLabel(),
                statistics.getValue(),
                statistics.getMetric().getUnit(),
                statistics.getYear());
    }

    public String getName()            { return name; }
    public String getLabel()           { return label; }
    public double getValue()           { return value; }
    public String getUnit()            { return unit; }
    public int getYear()               { return year; }

    public void setName(String name)   { this.name = name; }
    public void setLabel(String label) { this.label = label; }
    public void setValue(double value) { this.value = value; }
    public void setUnit(String unit)   { this.unit = unit; }
    public void setYear(int year)      { this.year = year; }
}