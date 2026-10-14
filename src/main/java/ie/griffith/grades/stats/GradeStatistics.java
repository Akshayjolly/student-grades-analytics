package ie.griffith.grades.stats;

import java.util.*;

/**
 * Pure statistics functions (no database, no servlets) so they are easy to unit test.
 * Covers what the Probability & Statistics module asks for: mean, median, standard deviation,
 * frequency distribution and a least-squares trend line used for a simple prediction.
 */
public final class GradeStatistics {
    private GradeStatistics() { }

    public static double mean(List<Double> xs) {
        requireData(xs);
        double sum = 0;
        for (double x : xs) sum += x;
        return sum / xs.size();
    }

    public static double median(List<Double> xs) {
        requireData(xs);
        List<Double> sorted = new ArrayList<>(xs);
        Collections.sort(sorted);
        int n = sorted.size();
        return n % 2 == 1 ? sorted.get(n / 2) : (sorted.get(n / 2 - 1) + sorted.get(n / 2)) / 2;
    }

    /** Sample standard deviation (divides by n - 1). Returns 0 for a single value. */
    public static double standardDeviation(List<Double> xs) {
        requireData(xs);
        if (xs.size() == 1) return 0;
        double m = mean(xs), squares = 0;
        for (double x : xs) squares += (x - m) * (x - m);
        return Math.sqrt(squares / (xs.size() - 1));
    }

    /** Count of scores in each band, in band order (First ... Fail). */
    public static Map<String, Integer> distribution(List<Double> xs) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (GradeBand b : GradeBand.values()) counts.put(b.label(), 0);
        for (double x : xs) counts.merge(GradeBand.of(x).label(), 1, Integer::sum);
        return counts;
    }

    /** Least-squares line y = intercept + slope * x. */
    public record Trend(double slope, double intercept) {
        public double predict(double x) {
            return intercept + slope * x;
        }
    }

    /**
     * Fits a straight line through (x, y) points, e.g. (semester, average score).
     * slope = sum((x - mx)(y - my)) / sum((x - mx)^2)
     */
    public static Trend linearTrend(List<Double> x, List<Double> y) {
        if (x.size() != y.size() || x.size() < 2) throw new IllegalArgumentException("need at least two points");
        double mx = mean(x), my = mean(y), num = 0, den = 0;
        for (int i = 0; i < x.size(); i++) {
            num += (x.get(i) - mx) * (y.get(i) - my);
            den += (x.get(i) - mx) * (x.get(i) - mx);
        }
        double slope = den == 0 ? 0 : num / den;
        return new Trend(slope, my - slope * mx);
    }

    /** Rounds to 1 decimal place for display. */
    public static double round1(double v) {
        return Math.round(v * 10) / 10.0;
    }

    private static void requireData(List<Double> xs) {
        if (xs == null || xs.isEmpty()) throw new IllegalArgumentException("no data");
    }
}
