package ie.griffith.grades.service;

import ie.griffith.grades.dao.GradeDao;
import ie.griffith.grades.model.Grade;
import ie.griffith.grades.stats.GradeStatistics;

import java.util.*;
import java.util.stream.Collectors;

import static ie.griffith.grades.stats.GradeStatistics.round1;

/** Turns raw grades into the reports shown on the dashboards. Returns plain maps/records that Gson turns into JSON. */
public class AnalyticsService {
    public static final double AT_RISK_BELOW = 40;

    private final GradeDao grades;

    public AnalyticsService(GradeDao grades) {
        this.grades = grades;
    }

    public record Summary(double mean, double median, double stdDev, double min, double max, int count) { }

    public record StudentReport(Summary overall, Map<String, Double> moduleAverages, Map<String, Integer> distribution,
                                Map<Integer, Double> semesterAverages, Double predictedNextSemester, List<Grade> grades) { }

    public record ModuleReport(String moduleCode, Summary summary, Map<String, Integer> distribution,
                               Map<Integer, Double> semesterAverages, int studentsAtRisk) { }

    public StudentReport studentReport(int studentId) {
        List<Grade> list = grades.findByStudent(studentId);
        if (list.isEmpty()) return new StudentReport(null, Map.of(), Map.of(), Map.of(), null, List.of());

        List<Double> scores = scores(list);
        Map<String, Double> byModule = new TreeMap<>();
        list.stream().collect(Collectors.groupingBy(Grade::moduleCode))
                .forEach((m, gs) -> byModule.put(m, round1(GradeStatistics.mean(scores(gs)))));

        Map<Integer, Double> bySemester = semesterAverages(list);
        return new StudentReport(summarise(scores), byModule, GradeStatistics.distribution(scores),
                bySemester, predictNext(bySemester), list);
    }

    public ModuleReport moduleReport(String moduleCode) {
        List<Grade> list = grades.findByModule(moduleCode);
        if (list.isEmpty()) return new ModuleReport(moduleCode, null, Map.of(), Map.of(), 0);

        // a student is "at risk" if their average in this module is below 40
        int atRisk = (int) list.stream().collect(Collectors.groupingBy(Grade::studentId)).values().stream()
                .filter(gs -> GradeStatistics.mean(scores(gs)) < AT_RISK_BELOW).count();
        List<Double> scores = scores(list);
        return new ModuleReport(moduleCode, summarise(scores), GradeStatistics.distribution(scores),
                semesterAverages(list), atRisk);
    }

    public List<String> modules() {
        return grades.findModuleCodes();
    }

    public Grade addGrade(Grade g) {
        if (g.score() < 0 || g.score() > 100) throw new IllegalArgumentException("score must be 0-100");
        if (g.semester() < 1) throw new IllegalArgumentException("semester must be 1 or more");
        if (g.moduleCode() == null || g.moduleCode().isBlank()) throw new IllegalArgumentException("module is required");
        return grades.save(g);
    }

    /** Uses the linear trend of semester averages to estimate the next semester (clamped to 0-100). */
    static Double predictNext(Map<Integer, Double> semesterAverages) {
        if (semesterAverages.size() < 2) return null;
        List<Double> x = new ArrayList<>(), y = new ArrayList<>();
        semesterAverages.forEach((s, avg) -> { x.add((double) s); y.add(avg); });
        int next = Collections.max(semesterAverages.keySet()) + 1;
        double p = GradeStatistics.linearTrend(x, y).predict(next);
        return round1(Math.max(0, Math.min(100, p)));
    }

    private static Map<Integer, Double> semesterAverages(List<Grade> list) {
        Map<Integer, Double> out = new TreeMap<>();
        list.stream().collect(Collectors.groupingBy(Grade::semester))
                .forEach((s, gs) -> out.put(s, round1(GradeStatistics.mean(scores(gs)))));
        return out;
    }

    private static Summary summarise(List<Double> s) {
        return new Summary(round1(GradeStatistics.mean(s)), round1(GradeStatistics.median(s)),
                round1(GradeStatistics.standardDeviation(s)), Collections.min(s), Collections.max(s), s.size());
    }

    private static List<Double> scores(List<Grade> list) {
        return list.stream().map(Grade::score).toList();
    }
}
