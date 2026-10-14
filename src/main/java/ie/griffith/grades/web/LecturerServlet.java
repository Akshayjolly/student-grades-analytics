package ie.griffith.grades.web;

import ie.griffith.grades.dao.UserDao;
import ie.griffith.grades.model.Grade;
import ie.griffith.grades.model.Role;
import ie.griffith.grades.service.AnalyticsService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

/**
 * Lecturer endpoints (role checked by AuthFilter):
 *   GET  /api/lecturer/modules            list of module codes
 *   GET  /api/lecturer/module?code=X      module statistics
 *   GET  /api/lecturer/students           list of students
 *   GET  /api/lecturer/student?id=N       one student's report
 *   POST /api/lecturer/grades             add a grade
 */
@WebServlet("/api/lecturer/*")
public class LecturerServlet extends HttpServlet {

    private AnalyticsService analytics() {
        return (AnalyticsService) getServletContext().getAttribute(AppContext.ANALYTICS);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getPathInfo() == null ? "" : req.getPathInfo();
        switch (action) {
            case "/modules" -> Json.write(resp, 200, analytics().modules());
            case "/module" -> {
                String code = req.getParameter("code");
                if (code == null) { Json.error(resp, 400, "code is required"); return; }
                Json.write(resp, 200, analytics().moduleReport(code));
            }
            case "/students" -> {
                UserDao users = (UserDao) getServletContext().getAttribute(AppContext.USERS);
                Json.write(resp, 200, users.findByRole(Role.STUDENT).stream()
                        .map(u -> Map.of("id", u.id(), "fullName", u.fullName())).toList());
            }
            case "/student" -> {
                try {
                    Json.write(resp, 200, analytics().studentReport(Integer.parseInt(req.getParameter("id"))));
                } catch (NumberFormatException e) {
                    Json.error(resp, 400, "id must be a number");
                }
            }
            default -> Json.error(resp, 404, "Unknown endpoint");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!"/grades".equals(req.getPathInfo())) {
            Json.error(resp, 404, "Unknown endpoint");
            return;
        }
        try {
            Grade g = Json.read(req, Grade.class);
            if (g == null) { Json.error(resp, 400, "request body is required"); return; }
            Json.write(resp, 201, analytics().addGrade(g));
        } catch (IllegalArgumentException | com.google.gson.JsonParseException e) {
            Json.error(resp, 400, e.getMessage());
        }
    }
}
