package userClasses.dto;

import java.util.List;

import userClasses.entities.Location;
import userClasses.entities.User;
import userClasses.entities.UserMetric;

public class UserUpdateDto {

    private Long id;
    private String username;
    private String email;
    private String name;
    private long locationId;
    private List<UserMetricDto> metrics;


    public UserUpdateDto() {
    }


    public UserUpdateDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.name = user.getName();
        if (user.getLocation() != null) {
            this.locationId = user.getLocation().getId();
        }
        if (user.getMetrics() != null) {
            this.metrics = user.getMetrics().stream()
                    .map(UserMetricDto::new)
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

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }


    public List<UserMetricDto> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<UserMetricDto> metrics) {
        this.metrics = metrics;
    }


}

