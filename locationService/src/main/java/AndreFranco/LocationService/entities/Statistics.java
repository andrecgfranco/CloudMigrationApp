package AndreFranco.LocationService.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "location_metrics")
public class Statistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metric_id", nullable = false)
    private Metric metric;

    @Column(name = "metric_value", nullable = false)
    private double value;

    @Column(nullable = false)
    private int year;

    public Long getId()                   { return id; }
    public Location getLocation()         { return location; }
    public Metric getMetric()             { return metric; }
    public Double getValue()          { return value; }
    public int getYear()                  { return year; }

    public void setId(Long id)            { this.id = id; }
    public void setLocation(Location l)   { this.location = l; }
    public void setMetric(Metric m)       { this.metric = m; }
    public void setValue(Double v)    { this.value = v; }
    public void setYear(int year)         { this.year = year; }
}
