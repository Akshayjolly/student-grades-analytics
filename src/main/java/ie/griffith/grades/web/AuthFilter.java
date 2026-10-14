package ie.griffith.grades.web;

import ie.griffith.grades.model.Role;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Protects the API: you must be logged in for /api/*, and you need the matching role
 * for /api/student/* and /api/lecturer/*. /api/login is public.
 */
@WebFilter("/api/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String path = req.getRequestURI().substring(req.getContextPath().length());

        if (path.equals("/api/login")) {
            chain.doFilter(request, response);
            return;
        }
        HttpSession session = req.getSession(false);
        SessionUser user = session == null ? null : (SessionUser) session.getAttribute(SessionUser.KEY);
        if (user == null) {
            Json.error(resp, 401, "Please log in");
            return;
        }
        Role required = requiredRole(path);
        if (required != null && user.role() != required) {
            Json.error(resp, 403, "You do not have access to this page");
            return;
        }
        chain.doFilter(request, response);
    }

    /** Which role a path needs, or null if any logged-in user may use it. */
    static Role requiredRole(String path) {
        if (path.startsWith("/api/student/")) return Role.STUDENT;
        if (path.startsWith("/api/lecturer/")) return Role.LECTURER;
        return null;
    }
}
