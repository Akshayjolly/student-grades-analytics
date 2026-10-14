package ie.griffith.grades.web;

import ie.griffith.grades.service.AnalyticsService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** GET /api/student/report - the logged-in student's own grades and statistics. */
@WebServlet("/api/student/report")
public class StudentReportServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SessionUser user = (SessionUser) req.getSession().getAttribute(SessionUser.KEY);
        AnalyticsService analytics = (AnalyticsService) getServletContext().getAttribute(AppContext.ANALYTICS);
        // the student id comes from the session, never from the URL, so students only see their own grades
        Json.write(resp, 200, analytics.studentReport(user.id()));
    }
}
