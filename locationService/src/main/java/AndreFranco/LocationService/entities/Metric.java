package AndreFranco.LocationService.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "metrics")
public class Metric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String label;

    @Column
    private String unit;

    public Long getId()               { return id; }
    public String getName()           { return name; }
    public String getLabel()          { return label; }
    public String getUnit()           { return unit; }

    public void setId(Long id)        { this.id = id; }
    public void setName(String name)  { this.name = name; }
    public void setLabel(String l)    { this.label = l; }
    public void setUnit(String unit)  { this.unit = unit; }
}