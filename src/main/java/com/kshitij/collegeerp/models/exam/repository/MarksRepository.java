package com.kshitij.collegeerp.models.exam.repository;

import com.kshitij.collegeerp.models.exam.entity.Marks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MarksRepository extends JpaRepository<Marks, Long> {
    List<Marks> findByExamId(Long examId);
    List<Marks> findByStudentId(Long studentId);
    Optional<Marks> findByExamIdAndStudentId(Long examId, Long studentId);
    boolean existsByExamIdAndStudentId(Long examId, Long studentId);
}
