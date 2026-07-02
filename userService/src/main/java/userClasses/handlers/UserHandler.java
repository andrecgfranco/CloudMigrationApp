package userClasses.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import userClasses.dto.UserDto;
import userClasses.dto.UserMetricDto;
import userClasses.dto.CountryScoreDto;
import userClasses.dto.FavoriteLocationRequest;
import userClasses.dto.LocationMetricDto;
import userClasses.dto.UserUpdateDto;
import userClasses.entities.Location;
import userClasses.entities.Metric;
import userClasses.entities.User;
import userClasses.entities.UserMetric;
import userClasses.repositories.UserRepository;
import userClasses.service.UserRPCClient;

@Component
public class UserHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRPCClient userRPCService;

    public UserDto mapToDto(User user) {
        return new UserDto(user);
    }

    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(this::mapToDto).collect(java.util.stream.Collectors.toList());
    }

    public List<UserDto> getUsersByName(String name) {
        Optional<List<User>> users = userRepository.findAllByName(name);
        return users.orElse(Collections.emptyList()).stream().map(this::mapToDto).collect(java.util.stream.Collectors.toList());
    }

    public UserDto getUserByUsername(String username) {
        Optional<User> user = userRepository.getUserByUsername(username);
        if(user.isPresent()) {
            return mapToDto(user.get());
        }
        return null;
    }

    public UserDto getUserById(long id) {
        Optional<User> user = userRepository.getUserById(id);
        if(user.isPresent()) {
            return mapToDto(user.get());
        }
        return null;
    }

    public UserDto updateUser(UserUpdateDto updatedUser) {

        Optional<User> user = userRepository.getUserById(updatedUser.getId());
        if (user.isEmpty()) {
            return null;
        }
        User currentUser = user.get();
        //Sequência de If's
        if (updatedUser.getUsername() != null) {
            Optional<User> userCheck = userRepository.getUserByUsername(updatedUser.getUsername());
            if (userCheck.isPresent() && userCheck.get().getId() != currentUser.getId()) {
                return null; 
            }
            currentUser.setUsername(updatedUser.getUsername());
        }

        if (updatedUser.getEmail() != null) {
            Optional<User> userCheck = userRepository.getUserByEmail(updatedUser.getEmail());
            if (userCheck.isPresent() && userCheck.get().getId() != currentUser.getId()) {
                return null;
            }
            currentUser.setEmail(updatedUser.getEmail());
        }

        if (updatedUser.getName() != null) {
            currentUser.setName(updatedUser.getName());
        }

        if (updatedUser.getLocationId() != 0) {
            Optional<Location> locationResponse = userRPCService.getLocationById(updatedUser.getLocationId());
            if (locationResponse.isPresent()) {
                currentUser.setLocation(locationResponse.get());
            }
        }
        
        
        if (updatedUser.getMetrics() != null && !updatedUser.getMetrics().isEmpty()) {
            for (UserMetricDto currentMetric : updatedUser.getMetrics()) {
                short value = currentMetric.getValue();
                Optional<Metric> metricResponse = userRPCService.getMetricByName(currentMetric.getName());
                if (metricResponse.isPresent()) {
                    if (value == 0) {
                        currentUser.removeMetric(metricResponse.get().getId());
                    } else {
                        currentUser.addOrUpdateMetric(metricResponse.get(), value);
                    }
                }
            }
        }

        userRepository.save(currentUser);
        return mapToDto(currentUser);
    }

    public UserDto addFavoriteLocation(FavoriteLocationRequest request) {
        long userId = request.getUserId();
        long locationId = request.getLocationId();
        Optional<User> existingUser = userRepository.getUserById(userId);
        if (existingUser.isEmpty()) {
            return null;
        }
        User user = existingUser.get();
        Location favoriteLocation = user.getFavoriteLocations().stream()
                .filter(loc -> loc.getId() == locationId)
                .findFirst()
                .orElse(null);
        
        if(favoriteLocation != null) {
            return mapToDto(user);
        }

        Optional<Location> locationResponse = userRPCService.getLocationById(locationId);
        if (locationResponse.isEmpty()) {
            return null;
        }
        user.addFavoriteLocation(locationResponse.get());
        userRepository.save(user);
        return mapToDto(user);
    }

    public UserDto removeFavoriteLocation(FavoriteLocationRequest request) {
        long userId = request.getUserId();
        long locationId = request.getLocationId();
        Optional<User> existingUser = userRepository.getUserById(userId);
        if (existingUser.isEmpty()) {
            return null;
        }
        User user = existingUser.get();
        user.removeFavoriteLocation(locationId);
        userRepository.save(user);
        return mapToDto(user);
    }

    
    //DEPRECADO
    public List<CountryScoreDto> getPersonalizedLocationList(long userId) {
        Optional<User> existingUser = userRepository.getUserById(userId);
        if (existingUser.isEmpty()) {
            return null;
        }
        User user = existingUser.get();
        List<CountryScoreDto> personalizedLocations = new ArrayList<>();
        List<UserMetric> userMetrics = user.getMetrics();
        for (UserMetric metric : userMetrics) {
            long metricId = metric.getMetric().getId();
            short userValue = metric.getMetricValue();
            List<LocationMetricDto> responseStream = userRPCService.getLocationMetricsOrderedByMetricId(metricId)
                    .orElseThrow(() -> new RuntimeException("MetricService unavailable"));
            int points = 5;
            for (LocationMetricDto locationMetric : responseStream) {
                double score = points * userValue;
                Optional<CountryScoreDto> existingLocation = personalizedLocations.stream()
                    .filter(loc -> loc.getCountry().equals(locationMetric.getLocationName()))
                    .findFirst();
                if (existingLocation.isPresent()) {
                    existingLocation.get().addToScore(score);
                } else {
                    personalizedLocations.add(new CountryScoreDto(locationMetric.getLocationName(), score));
                }
                if (points > 1) {
                    points--;
                }
            }

        };
        return personalizedLocations;
    }


}
