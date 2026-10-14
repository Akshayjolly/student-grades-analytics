package ie.griffith.grades.dao;

import ie.griffith.grades.model.Grade;

import java.util.List;

public interface GradeDao {
    List<Grade> findByStudent(int studentId);
    List<Grade> findByModule(String moduleCode);
    List<String> findModuleCodes();
    Grade save(Grade grade);
}
