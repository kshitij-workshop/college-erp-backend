package com.kshitij.collegeerp.models.exam.dto;

import com.kshitij.collegeerp.models.exam.entity.ExamType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class ExamRequest {

    @NotBlank(message = "Exam name is required")
    private String name;

    @NotNull(message = "Exam type is required")
    private ExamType examType;

    @NotNull(message = "Max marks is required")
    @Min(value = 1, message = "Max marks must be at least 1")
    private Integer maxMarks;

    @NotNull(message = "Subject offering id is required")
    private Long subjectOfferingId;
    private LocalDate examDate;
}
