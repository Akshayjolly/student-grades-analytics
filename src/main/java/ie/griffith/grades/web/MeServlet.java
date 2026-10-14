package ie.griffith.grades.web;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** GET /api/me - who is logged in (used by the pages to choose what to show). */
@WebServlet("/api/me")
public class MeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Json.write(resp, 200, req.getSession().getAttribute(SessionUser.KEY));
    }
}
