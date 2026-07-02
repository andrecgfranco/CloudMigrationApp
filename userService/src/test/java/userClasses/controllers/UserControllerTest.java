package userClasses.controllers;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import userClasses.dto.CountryScoreDto;
import userClasses.dto.FavoriteLocationRequest;
import userClasses.dto.UserDto;
import userClasses.dto.UserUpdateDto;
import userClasses.handlers.UserHandler;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController controller;

    @Mock
    private UserHandler handler;

 

    @Test
    void getAllUsers_always_returns200() {
        UserDto user = new UserDto();
        user.setId(1L);
        user.setUsername("testuser");
        when(handler.getAllUsers()).thenReturn(List.of(user));
        ResponseEntity<List<UserDto>> response = controller.getAllUsers(null);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void getAllUsers_withEmptyDatabase_returns200WithEmptyList() {
        when(handler.getAllUsers()).thenReturn(List.of());
        ResponseEntity<List<UserDto>> response = controller.getAllUsers(null);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEmpty();
    }



    @Test
    void getUsersByName_validName_returns200() {
        UserDto user = new UserDto();
        user.setId(1L);
        user.setName("John");
        when(handler.getUsersByName("John")).thenReturn(List.of(user));
        ResponseEntity<List<UserDto>> response = controller.getAllUsers("John");
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    void getUsersByName_noMatch_returns404() {
        when(handler.getUsersByName("NonExistent")).thenReturn(null);
        ResponseEntity<List<UserDto>> response = controller.getAllUsers("NonExistent");
        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }



    @Test
    void getUserByUsername_validUsername_returns200() {
        UserDto user = new UserDto();
        user.setId(1L);
        user.setUsername("testuser");
        when(handler.getUserByUsername("testuser")).thenReturn(user);
        ResponseEntity<UserDto> response = controller.getUserByUsername("testuser");
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    void getUserByUsername_missingUsername_returns404() {
        when(handler.getUserByUsername("nonexistent")).thenReturn(null);
        ResponseEntity<UserDto> response = controller.getUserByUsername("nonexistent");
        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }



    @Test
    void getUserById_validId_returns200() {
        UserDto user = new UserDto();
        user.setId(1L);
        when(handler.getUserById(1)).thenReturn(user);
        ResponseEntity<UserDto> response = controller.getUserById(1);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    void getUserById_missingId_returns404() {
        when(handler.getUserById(99)).thenReturn(null);
        ResponseEntity<UserDto> response = controller.getUserById(99);
        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }



    @Test
    void updateUser_validUpdate_returns200() {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setId(1L);
        updateDto.setUsername("newuser");
        UserDto updated = new UserDto();
        updated.setId(1L);
        updated.setUsername("newuser");
        when(handler.updateUser(updateDto)).thenReturn(updated);
        ResponseEntity<UserDto> response = controller.updateUser(updateDto);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    void updateUser_missingUser_returns404() {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setId(99L);
        when(handler.updateUser(updateDto)).thenReturn(null);
        ResponseEntity<UserDto> response = controller.updateUser(updateDto);
        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }

 

    @Test
    void addFavoriteLocation_validRequest_returns200() {
        FavoriteLocationRequest request = new FavoriteLocationRequest();
        request.setUserId(1L);
        request.setLocationId(1L);
        UserDto user = new UserDto();
        user.setId(1L);
        when(handler.addFavoriteLocation(request)).thenReturn(user);
        ResponseEntity<UserDto> response = controller.addFavoriteLocation(request);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    void addFavoriteLocation_missingUser_returns404() {
        FavoriteLocationRequest request = new FavoriteLocationRequest();
        request.setUserId(99L);
        request.setLocationId(1L);
        when(handler.addFavoriteLocation(request)).thenReturn(null);
        ResponseEntity<UserDto> response = controller.addFavoriteLocation(request);
        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }



    @Test
    void removeFavoriteLocation_validRequest_returns200() {
        FavoriteLocationRequest request = new FavoriteLocationRequest();
        request.setUserId(1L);
        request.setLocationId(1L);
        UserDto user = new UserDto();
        user.setId(1L);
        when(handler.removeFavoriteLocation(request)).thenReturn(user);
        ResponseEntity<UserDto> response = controller.removeFavoriteLocation(request);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }


}
