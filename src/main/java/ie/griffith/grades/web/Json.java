package ie.griffith.grades.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

/** Small helpers for reading and writing JSON in servlets. */
public final class Json {
    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    private Json() { }

    public static void write(HttpServletResponse resp, int status, Object body) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(GSON.toJson(body));
    }

    public static void error(HttpServletResponse resp, int status, String message) throws IOException {
        write(resp, status, Map.of("error", message));
    }

    public static <T> T read(HttpServletRequest req, Class<T> type) throws IOException {
        return GSON.fromJson(req.getReader(), type);
    }
}
