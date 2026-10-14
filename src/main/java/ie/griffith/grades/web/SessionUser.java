package ie.griffith.grades.web;

import ie.griffith.grades.model.Role;

import java.io.Serializable;

/** What we keep in the HTTP session after login (no password hash). */
public record SessionUser(int id, String username, String fullName, Role role) implements Serializable {
    public static final String KEY = "user";
}
