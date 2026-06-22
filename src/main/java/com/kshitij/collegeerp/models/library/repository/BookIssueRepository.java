package com.kshitij.collegeerp.models.library.repository;

import com.kshitij.collegeerp.models.library.entity.BookIssue;
import com.kshitij.collegeerp.models.library.entity.IssueStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookIssueRepository extends JpaRepository<BookIssue, Long> {
    List<BookIssue> findByStudentId(Long studentId);
    List<BookIssue> findByBookId(Long bookId);
    List<BookIssue> findByStatus(IssueStatus status);
    List<BookIssue> findByStudentIdAndStatus(Long studentId, IssueStatus status);

    // Overdue books find
    List<BookIssue> findByStatusAndDueDateBefore(IssueStatus status, LocalDate date);

    // if a student has already same book issued
    boolean existsByStudentIdAndBookIdAndStatus(
            Long studentId, Long bookId, IssueStatus status);
}
