package ie.griffith.grades.dao;

import ie.griffith.grades.model.Grade;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcGradeDao implements GradeDao {
    private final Database db;

    public JdbcGradeDao(Database db) {
        this.db = db;
    }

    @Override
    public List<Grade> findByStudent(int studentId) {
        return query("SELECT * FROM grade WHERE student_id = ? ORDER BY semester, module_code", ps -> ps.setInt(1, studentId));
    }

    @Override
    public List<Grade> findByModule(String moduleCode) {
        return query("SELECT * FROM grade WHERE module_code = ? ORDER BY semester", ps -> ps.setString(1, moduleCode));
    }

    @Override
    public List<String> findModuleCodes() {
        List<String> codes = new ArrayList<>();
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT module_code FROM module ORDER BY module_code");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) codes.add(rs.getString(1));
            return codes;
        } catch (SQLException e) {
            throw new IllegalStateException("Database error", e);
        }
    }

    @Override
    public Grade save(Grade g) {
        String sql = "INSERT INTO grade (student_id, module_code, assessment, semester, score) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, g.studentId());
            ps.setString(2, g.moduleCode());
            ps.setString(3, g.assessment());
            ps.setInt(4, g.semester());
            ps.setDouble(5, g.score());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return new Grade(keys.getInt(1), g.studentId(), g.moduleCode(), g.assessment(), g.semester(), g.score());
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not save grade", e);
        }
    }

    private interface Binder {
        void bind(PreparedStatement ps) throws SQLException;
    }

    private List<Grade> query(String sql, Binder binder) {
        List<Grade> grades = new ArrayList<>();
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    grades.add(new Grade(rs.getInt("grade_id"), rs.getInt("student_id"), rs.getString("module_code"),
                            rs.getString("assessment"), rs.getInt("semester"), rs.getDouble("score")));
                }
            }
            return grades;
        } catch (SQLException e) {
            throw new IllegalStateException("Database error", e);
        }
    }
}
