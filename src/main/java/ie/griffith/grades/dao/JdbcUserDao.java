package ie.griffith.grades.dao;

import ie.griffith.grades.model.Role;
import ie.griffith.grades.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcUserDao implements UserDao {
    private final Database db;

    public JdbcUserDao(Database db) {
        this.db = db;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM app_user WHERE username = ?";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Database error", e);
        }
    }

    @Override
    public List<User> findByRole(Role role) {
        String sql = "SELECT * FROM app_user WHERE role = ? ORDER BY full_name";
        List<User> users = new ArrayList<>();
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, role.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) users.add(map(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new IllegalStateException("Database error", e);
        }
    }

    private User map(ResultSet rs) throws SQLException {
        return new User(rs.getInt("user_id"), rs.getString("username"), rs.getString("password_hash"),
                rs.getString("full_name"), Role.valueOf(rs.getString("role")));
    }
}
