package com.kshitij.collegeerp.models.assignment.dto;

import com.kshitij.collegeerp.models.assignment.entity.SubmissionStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentAssignmentResponse {

    private Long assignmentId;

    private String title;

    private String description;

    private String subjectName;

    private String subjectCode;

    private String facultyName;

    private LocalDateTime dueDate;

    private Integer maxMarks;

    private SubmissionStatus submissionStatus;

    private LocalDateTime submittedAt;

    private Double marksAwarded;

    private String feedback;

    private boolean late;

    private String attachmentName;

    private String attachmentUrl;
}