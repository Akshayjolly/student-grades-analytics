package ie.griffith.grades;

import ie.griffith.grades.stats.GradeBand;
import ie.griffith.grades.stats.GradeStatistics;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GradeStatisticsTest {
    private final List<Double> scores = List.of(45.0, 72.0, 58.0, 63.0, 38.0, 81.0);

    @Test
    void meanAndMedian() {
        assertEquals(59.5, GradeStatistics.mean(scores), 1e-9);
        assertEquals(60.5, GradeStatistics.median(scores), 1e-9);      // (58 + 63) / 2
        assertEquals(58.0, GradeStatistics.median(List.of(70.0, 58.0, 40.0)), 1e-9);
    }

    @Test
    void sampleStandardDeviation() {
        // values 2,4,4,4,5,5,7,9 have sample SD = 2.138...
        List<Double> xs = List.of(2.0, 4.0, 4.0, 4.0, 5.0, 5.0, 7.0, 9.0);
        assertEquals(2.13809, GradeStatistics.standardDeviation(xs), 1e-4);
        assertEquals(0, GradeStatistics.standardDeviation(List.of(50.0)));
    }

    @Test
    void distributionUsesBands() {
        Map<String, Integer> d = GradeStatistics.distribution(scores);
        assertEquals(2, d.get("First (70+)"));
        assertEquals(1, d.get("2.1 (60-69)"));
        assertEquals(1, d.get("2.2 (50-59)"));
        assertEquals(1, d.get("Pass (40-49)"));
        assertEquals(1, d.get("Fail (<40)"));
        assertEquals(GradeBand.FIRST, GradeBand.of(70));
        assertEquals(GradeBand.FAIL, GradeBand.of(39.9));
    }

    @Test
    void linearTrendFitsAPerfectLine() {
        GradeStatistics.Trend t = GradeStatistics.linearTrend(List.of(1.0, 2.0, 3.0), List.of(50.0, 55.0, 60.0));
        assertEquals(5.0, t.slope(), 1e-9);
        assertEquals(45.0, t.intercept(), 1e-9);
        assertEquals(65.0, t.predict(4), 1e-9);
    }

    @Test
    void emptyDataIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> GradeStatistics.mean(List.of()));
        assertThrows(IllegalArgumentException.class, () -> GradeStatistics.linearTrend(List.of(1.0), List.of(1.0)));
    }
}
