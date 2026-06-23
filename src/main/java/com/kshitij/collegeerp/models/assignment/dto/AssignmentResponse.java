package com.kshitij.collegeerp.models.assignment.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class AssignmentResponse {
    private Long id;
    private String title;
    private String description;
    private Long subjectOfferingId;
    private String subjectName;
    private String sectionName;
    private String facultyName;
    private LocalDateTime dueDate;
    private Integer maxMarks;
    private boolean active;
}
