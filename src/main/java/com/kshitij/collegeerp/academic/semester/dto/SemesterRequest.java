package com.kshitij.collegeerp.academic.semester.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SemesterRequest {

    @NotNull(message = "Semester number is required")
    @Min(value = 1, message = "Semester number must be at least 1")
    private Integer semesterNumber;

    @NotNull(message = "Batch id is required")
    private Long batchId;

    private boolean current = false;

}
