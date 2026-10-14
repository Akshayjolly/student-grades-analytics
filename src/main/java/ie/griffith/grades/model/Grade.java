package ie.griffith.grades.model;

/**
 * One assessment result.
 * @param semester ordering number used for trends (1 = first semester, 2 = second, ...)
 */
public record Grade(int id, int studentId, String moduleCode, String assessment, int semester, double score) { }
