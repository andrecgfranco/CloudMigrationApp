package AndreFranco.LocationService.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String code;

    public Long getId()               { return id; }
    public String getName()           { return name; }
    public String getCode()           { return code; }

    public void setId(Long id)        { this.id = id; }
    public void setName(String name)  { this.name = name; }
    public void setCode(String code)  { this.code = code; }
}