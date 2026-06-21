package com.kshitij.collegeerp.models.student.repository;

import com.kshitij.collegeerp.models.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEnrollmentNumber(String enrollmentNumber);
    Optional<Student> findByEmail(String email);
    boolean existsByEnrollmentNumber(String enrollmentNumber);
    boolean existsByEmail(String email);
    List<Student> findBySectionId(Long sectionId);
    List<Student> findBySemesterId(Long semesterId);
    List<Student> findByBatchId(Long batchId);
    List<Student> findByDepartmentId(Long departmentId);

}
