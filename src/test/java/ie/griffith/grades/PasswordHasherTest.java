package ie.griffith.grades;

import ie.griffith.grades.security.PasswordHasher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {
    @Test
    void correctPasswordVerifies() {
        String stored = PasswordHasher.hash("secret");
        assertTrue(PasswordHasher.verify("secret", stored));
        assertFalse(PasswordHasher.verify("Secret", stored));
    }

    @Test
    void samePasswordGetsDifferentSalt() {
        assertNotEquals(PasswordHasher.hash("secret"), PasswordHasher.hash("secret"));
    }

    @Test
    void hashDoesNotContainPlainText() {
        assertFalse(PasswordHasher.hash("mypassword").contains("mypassword"));
    }

    @Test
    void malformedHashIsRejected() {
        assertFalse(PasswordHasher.verify("x", "not-a-hash"));
        assertFalse(PasswordHasher.verify(null, "1:a:b"));
    }
}
