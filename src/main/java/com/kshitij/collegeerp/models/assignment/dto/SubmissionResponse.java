package com.kshitij.collegeerp.models.assignment.dto;

import com.kshitij.collegeerp.models.assignment.entity.SubmissionStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionResponse {
    private Long id;
    private Long assignmentId;
    private String assignmentTitle;
    private Long studentId;
    private String studentName;
    private Long registrationNumber;
    private String submissionText;
    private LocalDateTime submittedAt;
    private boolean late;
    private Double marksAwarded;
    private String feedback;
    private SubmissionStatus status;
}
