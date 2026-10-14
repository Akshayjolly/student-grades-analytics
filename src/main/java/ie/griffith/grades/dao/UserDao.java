package ie.griffith.grades.dao;

import ie.griffith.grades.model.Role;
import ie.griffith.grades.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<User> findByUsername(String username);
    List<User> findByRole(Role role);
}
