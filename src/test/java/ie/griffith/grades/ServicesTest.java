package ie.griffith.grades;

import ie.griffith.grades.dao.GradeDao;
import ie.griffith.grades.dao.UserDao;
import ie.griffith.grades.model.Grade;
import ie.griffith.grades.model.Role;
import ie.griffith.grades.model.User;
import ie.griffith.grades.security.PasswordHasher;
import ie.griffith.grades.service.AnalyticsService;
import ie.griffith.grades.service.AuthService;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/** Tests the services with in-memory fake DAOs - no database needed. */
class ServicesTest {

    static class FakeGradeDao implements GradeDao {
        final List<Grade> grades = new ArrayList<>();
        public List<Grade> findByStudent(int id) { return grades.stream().filter(g -> g.studentId() == id).toList(); }
        public List<Grade> findByModule(String m) { return grades.stream().filter(g -> g.moduleCode().equals(m)).toList(); }
        public List<String> findModuleCodes() { return grades.stream().map(Grade::moduleCode).distinct().sorted().toList(); }
        public Grade save(Grade g) { grades.add(g); return g; }
    }

    private FakeGradeDao seeded() {
        FakeGradeDao dao = new FakeGradeDao();
        dao.save(new Grade(1, 1, "CS201", "CA1", 1, 50));
        dao.save(new Grade(2, 1, "CS202", "CA1", 1, 60));
        dao.save(new Grade(3, 1, "CS201", "CA2", 2, 60));
        dao.save(new Grade(4, 1, "CS202", "CA2", 2, 70));
        dao.save(new Grade(5, 2, "CS201", "CA1", 1, 30));
        dao.save(new Grade(6, 2, "CS201", "CA2", 2, 35));
        return dao;
    }

    @Test
    void studentReportHasAveragesTrendAndPrediction() {
        AnalyticsService s = new AnalyticsService(seeded());
        AnalyticsService.StudentReport r = s.studentReport(1);
        assertEquals(60.0, r.overall().mean());
        assertEquals(Map.of("CS201", 55.0, "CS202", 65.0), r.moduleAverages());
        assertEquals(Map.of(1, 55.0, 2, 65.0), r.semesterAverages());
        assertEquals(75.0, r.predictedNextSemester());   // +10 per semester
        assertEquals(4, r.grades().size());
    }

    @Test
    void studentWithNoGradesGetsEmptyReport() {
        assertNull(new AnalyticsService(seeded()).studentReport(99).overall());
    }

    @Test
    void moduleReportCountsStudentsAtRisk() {
        AnalyticsService.ModuleReport r = new AnalyticsService(seeded()).moduleReport("CS201");
        assertEquals(1, r.studentsAtRisk());       // student 2 averages 32.5
        assertEquals(4, r.summary().count());
    }

    @Test
    void invalidGradesAreRejected() {
        AnalyticsService s = new AnalyticsService(new FakeGradeDao());
        assertThrows(IllegalArgumentException.class, () -> s.addGrade(new Grade(0, 1, "CS201", "x", 1, 101)));
        assertThrows(IllegalArgumentException.class, () -> s.addGrade(new Grade(0, 1, "CS201", "x", 0, 50)));
    }

    @Test
    void loginChecksHashedPassword() {
        User akshay = new User(1, "akshay", PasswordHasher.hash("password123"), "Akshay Jolly", Role.STUDENT);
        UserDao users = new UserDao() {
            public Optional<User> findByUsername(String u) { return u.equals("akshay") ? Optional.of(akshay) : Optional.empty(); }
            public List<User> findByRole(Role r) { return List.of(akshay); }
        };
        AuthService auth = new AuthService(users);
        assertTrue(auth.login("akshay", "password123").isPresent());
        assertTrue(auth.login("akshay", "wrong").isEmpty());
        assertTrue(auth.login("nobody", "password123").isEmpty());
    }
}
