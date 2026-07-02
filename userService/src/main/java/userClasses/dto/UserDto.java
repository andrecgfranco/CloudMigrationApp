package userClasses.dto;

import java.util.List;

import userClasses.entities.Location;
import userClasses.entities.User;
import userClasses.entities.UserMetric;

public class UserDto {

    private Long id;
    private String username;
    private String email;
    private String name;
    private long locationId;
    private String locationName;
    private List<UserMetricDto> metrics;
    private List<LocationDto> favoriteLocations;


    public UserDto() {
    }


    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.name = user.getName();
        if (user.getLocation() != null) {
            this.locationId = user.getLocation().getId();
            this.locationName = user.getLocation().getName();
        }
        if (user.getMetrics() != null) {
            this.metrics = user.getMetrics().stream()
                    .map(UserMetricDto::new)
                    .toList();
        }
        if (user.getFavoriteLocations() != null) {
            this.favoriteLocations = user.getFavoriteLocations().stream()
                    .map(LocationDto::new)
                    .toList();
        }
        
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

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public List<UserMetricDto> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<UserMetricDto> metrics) {
        this.metrics = metrics;
    }

    public List<LocationDto> getFavoriteLocations() {
        return favoriteLocations;
    }

    public void setFavoriteLocations(List<LocationDto> favoriteLocations) {
        this.favoriteLocations = favoriteLocations;
    }


}

