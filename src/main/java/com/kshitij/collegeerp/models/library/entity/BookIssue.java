package com.kshitij.collegeerp.models.library.entity;

import com.kshitij.collegeerp.models.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "book_issues")
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class BookIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private LocalDate issueDate;

    @Column(nullable = false)
    private LocalDate dueDate; // issueDate + 14 days by default

    private LocalDate returnDate; // null while not returned

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueStatus status;

    private Double fineAmount;
}
