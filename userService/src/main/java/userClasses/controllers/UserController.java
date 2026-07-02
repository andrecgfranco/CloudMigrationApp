package userClasses.controllers;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import userClasses.dto.UserDto;
import userClasses.dto.UserUpdateDto;
import userClasses.dto.CountryScoreDto;
import userClasses.dto.FavoriteLocationRequest;
import userClasses.handlers.UserHandler;

/**
 * Retorna todos os users, ou users com um nome espcífico
 */
@RestController
@RequestMapping("/api/v1/users")
@Api(value = "Users API", tags = "Users")
public class UserController {

    @Autowired
    private UserHandler userHandler;

    @GetMapping
    @ApiOperation(value = "Get all users", notes = "Returns a list of all users.")
    public ResponseEntity<List<UserDto>> getAllUsers(@RequestParam(name = "name", required = false) String nome) {
        if (nome != null) {
            List<UserDto> userDto = userHandler.getUsersByName(nome);
            if (userDto != null && !userDto.isEmpty()) {
                return ResponseEntity.ok(userDto);
            }
            return ResponseEntity.notFound().build();
        }

        List<UserDto> users = userHandler.getAllUsers();

        return ResponseEntity.ok(users);
    }


    @GetMapping("/byUserName/{username}")
    @ApiOperation(value = "Get user by username", notes = "Returns a user given its username.")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable("username") String username) {
        UserDto userDto = userHandler.getUserByUsername(username);
        if (userDto != null) {
            return ResponseEntity.ok(userDto);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/byId/{id}")
    @ApiOperation(value = "Get user by Id", notes = "Returns a user given its Id.")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") int id) {
        UserDto userDto = userHandler.getUserById(id);
        if (userDto != null) {
            return ResponseEntity.ok(userDto);
        }
        return ResponseEntity.notFound().build();
    }


    @PutMapping("/update")
    @ApiOperation(value = "Update User", notes = "Updates a User.")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserUpdateDto userDto) {
        UserDto updatedUser = userHandler.updateUser(userDto);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser); // 200 OK
        }
        return ResponseEntity.notFound().build(); // 404 Not Found
    }


    @PutMapping("/addFavoriteLocation")
    @ApiOperation(value = "Add Favorite Location", notes = "Adds a favorite location to a user.")
    public ResponseEntity<UserDto> addFavoriteLocation(@RequestBody FavoriteLocationRequest request) {
        UserDto updatedUser = userHandler.addFavoriteLocation(request);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser); // 200 OK
        }
        return ResponseEntity.notFound().build(); // 404 Not Found

    }

    @PutMapping("/removeFavoriteLocation")
    @ApiOperation(value = "Remove Favorite Location", notes = "Removes a favorite location from a user.")
    public ResponseEntity<UserDto> removeFavoriteLocation(@RequestBody FavoriteLocationRequest request) {
        UserDto updatedUser = userHandler.removeFavoriteLocation(request);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser); // 200 OK
        }
        return ResponseEntity.notFound().build(); // 404 Not Found
    }

    
    @GetMapping("/personalizedList/{userId}")
    @ApiOperation(value = "Get Personalized List", notes = "Returns a personalized list of locations for a user.")
    public ResponseEntity<List<CountryScoreDto>> getPersonalizedList(@PathVariable("userId") int userId) {
        List<CountryScoreDto> personalizedList = userHandler.getPersonalizedLocationList(userId);
        if (personalizedList != null) {
            return ResponseEntity.ok(personalizedList);
        }
        return ResponseEntity.notFound().build();
    }
    
}