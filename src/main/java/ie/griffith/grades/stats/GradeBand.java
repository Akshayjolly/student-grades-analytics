package ie.griffith.grades.stats;

/** Irish/UK degree classification bands used for grade distributions. */
public enum GradeBand {
    FIRST("First (70+)", 70),
    UPPER_SECOND("2.1 (60-69)", 60),
    LOWER_SECOND("2.2 (50-59)", 50),
    PASS("Pass (40-49)", 40),
    FAIL("Fail (<40)", 0);

    private final String label;
    private final int minimum;

    GradeBand(String label, int minimum) {
        this.label = label;
        this.minimum = minimum;
    }

    public String label() {
        return label;
    }

    public static GradeBand of(double score) {
        for (GradeBand b : values()) if (score >= b.minimum) return b;
        return FAIL;
    }
}
