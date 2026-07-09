package com.kshitij.collegeerp.academic.semester.repository;

import com.kshitij.collegeerp.academic.semester.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SemesterRepository extends JpaRepository<Semester, Long> {
    List<Semester> findByBatchId(Long id);
    boolean existsBySemesterNumberAndBatchId(Integer semesterNumber, Long batchId);
    Optional<Semester> findByBatchIdAndCurrentTrue(Long batchId);
}
