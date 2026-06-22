package com.kshitij.collegeerp.models.exam.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.nio.DoubleBuffer;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResultResponse {
    private Long studentId;
    private String studentName;
    private String enrollmentNumber;
    private Double marksObtained;
    private Integer maxMarks;
    private String examName;
}
