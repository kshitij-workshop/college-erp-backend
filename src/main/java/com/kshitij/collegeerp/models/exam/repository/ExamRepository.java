package com.kshitij.collegeerp.models.exam.repository;

import com.kshitij.collegeerp.models.exam.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findBySubjectOfferingId(Long subjectOfferingId);
}
