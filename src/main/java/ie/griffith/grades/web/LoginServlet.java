package ie.griffith.grades.web;

import ie.griffith.grades.model.User;
import ie.griffith.grades.service.AuthService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/** POST /api/login  {"username": "...", "password": "..."} */
@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {
    private record LoginRequest(String username, String password) { }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LoginRequest body = Json.read(req, LoginRequest.class);
        AuthService auth = (AuthService) getServletContext().getAttribute(AppContext.AUTH);
        Optional<User> user = body == null ? Optional.empty() : auth.login(body.username(), body.password());
        if (user.isEmpty()) {
            Json.error(resp, 401, "Wrong username or password");
            return;
        }
        // new session id after login prevents session fixation
        HttpSession old = req.getSession(false);
        if (old != null) old.invalidate();
        User u = user.get();
        SessionUser su = new SessionUser(u.id(), u.username(), u.fullName(), u.role());
        req.getSession(true).setAttribute(SessionUser.KEY, su);
        Json.write(resp, 200, Map.of("fullName", su.fullName(), "role", su.role()));
    }
}
