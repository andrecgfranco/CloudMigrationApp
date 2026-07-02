package userClasses.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import userClasses.dto.FavoriteLocationRequest;
import userClasses.dto.UserDto;
import userClasses.dto.UserUpdateDto;
import userClasses.entities.Location;
import userClasses.entities.Metric;
import userClasses.entities.User;
import userClasses.entities.UserMetric;
import userClasses.repositories.UserRepository;
import userClasses.service.UserRPCClient;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class UserHandlerTest {

    @InjectMocks
    private UserHandler handler;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRPCClient userRPCClient;

    private User testUser;
    private Location testLocation;
    private Metric testMetric;
    private UserMetric userMetric;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");

        testLocation = new Location();
        testLocation.setId(1L);
        testLocation.setName("Portugal");

        testMetric = new Metric();
        testMetric.setId(1L);
        testMetric.setName("gdp");

        userMetric = new UserMetric();
        userMetric.setMetric(testMetric);
        userMetric.setMetricValue((short) 5);
    }

    @Test
    void getAllUsers_returnsAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));

        List<UserDto> result = handler.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("testuser");
    }

    @Test
    void getAllUsers_emptyDatabase_returnsEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        assertThat(handler.getAllUsers()).isEmpty();
    }

    @Test
    void getUsersByName_existingName_returnsUsers() {
        when(userRepository.findAllByName("Test User")).thenReturn(Optional.of(List.of(testUser)));

        List<UserDto> result = handler.getUsersByName("Test User");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test User");
    }

    @Test
    void getUsersByName_noMatch_returnsEmptyList() {
        when(userRepository.findAllByName("NonExistent")).thenReturn(Optional.empty());

        List<UserDto> result = handler.getUsersByName("NonExistent");

        assertThat(result).isEmpty();
    }

    @Test
    void getUserByUsername_validUsername_returnsUser() {
        when(userRepository.getUserByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserDto result = handler.getUserByUsername("testuser");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
    }

    @Test
    void getUserByUsername_missingUsername_returnsNull() {
        when(userRepository.getUserByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThat(handler.getUserByUsername("nonexistent")).isNull();
    }

    @Test
    void getUserById_validId_returnsUser() {
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(testUser));

        UserDto result = handler.getUserById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getUserById_missingId_returnsNull() {
        when(userRepository.getUserById(99L)).thenReturn(Optional.empty());

        assertThat(handler.getUserById(99L)).isNull();
    }

    @Test
    void updateUser_validUpdate_returnsUpdatedUser() {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setId(1L);
        updateDto.setUsername("newuser");

        when(userRepository.getUserById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.getUserByUsername("newuser")).thenReturn(Optional.empty());

        UserDto result = handler.updateUser(updateDto);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("newuser");
    }

    @Test
    void updateUser_missingUser_returnsNull() {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setId(99L);

        when(userRepository.getUserById(99L)).thenReturn(Optional.empty());

        assertThat(handler.updateUser(updateDto)).isNull();
    }

    @Test
    void updateUser_duplicateUsername_returnsNull() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("existinguser");

        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setId(1L);
        updateDto.setUsername("existinguser");

        when(userRepository.getUserById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.getUserByUsername("existinguser")).thenReturn(Optional.of(anotherUser));

        assertThat(handler.updateUser(updateDto)).isNull();
    }

    @Test
    void addFavoriteLocation_validRequest_returnsUpdatedUser() {
        FavoriteLocationRequest request = new FavoriteLocationRequest();
        request.setUserId(1L);
        request.setLocationId(1L);

        when(userRepository.getUserById(1L)).thenReturn(Optional.of(testUser));
        when(userRPCClient.getLocationById(1L)).thenReturn(Optional.of(testLocation));

        UserDto result = handler.addFavoriteLocation(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void addFavoriteLocation_missingUser_returnsNull() {
        FavoriteLocationRequest request = new FavoriteLocationRequest();
        request.setUserId(99L);
        request.setLocationId(1L);

        when(userRepository.getUserById(99L)).thenReturn(Optional.empty());

        assertThat(handler.addFavoriteLocation(request)).isNull();
    }

    @Test
    void removeFavoriteLocation_validRequest_returnsUpdatedUser() {
        FavoriteLocationRequest request = new FavoriteLocationRequest();
        request.setUserId(1L);
        request.setLocationId(1L);

        when(userRepository.getUserById(1L)).thenReturn(Optional.of(testUser));

        UserDto result = handler.removeFavoriteLocation(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }
}
