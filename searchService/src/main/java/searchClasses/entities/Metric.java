package searchClasses.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "metrics")
public class Metric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    private String label;

    @Column(length = 30)
    private String unit;

    public Metric() {}

    public Metric(String name, String label, String unit) {
        this.name = name;
        this.label = label;
        this.unit = unit;
    }

    public Long getId()               { return id; }
    public String getName()           { return name; }
    public String getLabel()          { return label; }
    public String getUnit()           { return unit; }

    public void setId(Long id)        { this.id = id; }
    public void setName(String name)  { this.name = name; }
    public void setLabel(String l)    { this.label = l; }
    public void setUnit(String unit)  { this.unit = unit; }
}
