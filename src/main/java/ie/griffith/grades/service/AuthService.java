package ie.griffith.grades.service;

import ie.griffith.grades.dao.UserDao;
import ie.griffith.grades.model.User;
import ie.griffith.grades.security.PasswordHasher;

import java.util.Optional;

/** Checks a username/password against the stored hash. */
public class AuthService {
    private final UserDao users;

    public AuthService(UserDao users) {
        this.users = users;
    }

    public Optional<User> login(String username, String password) {
        if (username == null || password == null) return Optional.empty();
        return users.findByUsername(username.trim())
                .filter(u -> PasswordHasher.verify(password, u.passwordHash()));
    }
}
