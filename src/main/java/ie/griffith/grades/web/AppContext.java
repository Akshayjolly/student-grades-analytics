package ie.griffith.grades.web;

import ie.griffith.grades.dao.Database;
import ie.griffith.grades.dao.JdbcGradeDao;
import ie.griffith.grades.dao.JdbcUserDao;
import ie.griffith.grades.dao.UserDao;
import ie.griffith.grades.service.AnalyticsService;
import ie.griffith.grades.service.AuthService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Wires the application together once when Tomcat starts (a tiny hand-made "dependency injection")
 * and stores the services in the ServletContext so every servlet can use them.
 */
@WebListener
public class AppContext implements ServletContextListener {
    static final String AUTH = "authService";
    static final String ANALYTICS = "analyticsService";
    static final String USERS = "userDao";

    @Override
    public void contextInitialized(ServletContextEvent event) {
        Database db = new Database();
        UserDao users = new JdbcUserDao(db);
        ServletContext ctx = event.getServletContext();
        ctx.setAttribute(USERS, users);
        ctx.setAttribute(AUTH, new AuthService(users));
        ctx.setAttribute(ANALYTICS, new AnalyticsService(new JdbcGradeDao(db)));
    }
}
