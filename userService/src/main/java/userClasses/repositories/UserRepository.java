package userClasses.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import userClasses.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<List<User>> findAllByName(String name);

    Optional<User> getUserById(long id);

    Optional<User> getUserByUsername(String username);

    Optional<User> getUserByEmail(String email);
}