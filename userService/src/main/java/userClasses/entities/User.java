package userClasses.entities;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    @Column
    private String name;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Location location;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserMetric> metrics = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "user_favorite_locations",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "location_id")
    )
    private List<Location> favoriteLocations = new ArrayList<>();


    public User() {
    }

    
    public User(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<UserMetric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<UserMetric> metrics) {
        this.metrics = metrics;
    }

    public void removeMetric(long metricId) {
        metrics.removeIf(um -> um.getMetric().getId() == metricId);
    }

    public void addOrUpdateMetric(Metric metric, short value) {
        for (UserMetric um : metrics) {
            if (um.getMetric().getId() == metric.getId()) {
                um.setMetricValue(value);
                return;
            }
        }
        UserMetric newUserMetric = new UserMetric(this, metric, value);
        metrics.add(newUserMetric);
    }


    public List<Location> getFavoriteLocations() {
        return favoriteLocations;
    }

    public void setFavoriteLocations(List<Location> favoriteLocations) {
        this.favoriteLocations = favoriteLocations;
    }

    public void addFavoriteLocation(Location location) {
        if (!favoriteLocations.contains(location)) {
            favoriteLocations.add(location);
        }
    }

    public void removeFavoriteLocation(long locationId) {
        favoriteLocations.removeIf(loc -> loc.getId() == locationId);
    }


}
