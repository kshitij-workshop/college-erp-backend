package com.kshitij.collegeerp.models.subject.repository;

import com.kshitij.collegeerp.models.subject.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findByCode(String code);
    boolean existsByCode(String code);
    List<Subject> findByProgramIdAndSemesterNumber(Long id, Integer semesterNumber);
}
