package com.kshitij.collegeerp.models.library.dto;

import com.kshitij.collegeerp.models.library.entity.IssueStatus;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class BookIssueResponse {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private String bookIsbn;
    private Long studentId;
    private String studentName;
    private String enrollmentNumber;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private IssueStatus status;
    private Double fineAmount;
}