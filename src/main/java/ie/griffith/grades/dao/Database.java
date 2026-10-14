package ie.griffith.grades.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Opens JDBC connections. Settings come from environment variables (set in docker-compose.yml),
 * with defaults for running MySQL locally.
 */
public class Database {
    private final String url = env("DB_URL", "jdbc:mysql://localhost:3306/grades");
    private final String user = env("DB_USER", "grades");
    private final String password = env("DB_PASSWORD", "grades");

    public Database() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // needed inside Tomcat's class loader
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("MySQL driver missing", e);
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    private static String env(String name, String fallback) {
        String v = System.getenv(name);
        return v == null || v.isBlank() ? fallback : v;
    }
}
