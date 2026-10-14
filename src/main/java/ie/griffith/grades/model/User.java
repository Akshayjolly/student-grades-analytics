package ie.griffith.grades.model;

/** A user account. passwordHash is never sent to the browser. */
public record User(int id, String username, String passwordHash, String fullName, Role role) { }
