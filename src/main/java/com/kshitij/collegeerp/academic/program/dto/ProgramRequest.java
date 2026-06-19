package com.kshitij.collegeerp.academic.program.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProgramRequest {

    @NotBlank(message = "Program name is required")
    private String name;

    @NotBlank(message = "Program code is required")
    private String code;

    @NotNull(message = "Duration in year is required")
    @Min(value = 1, message = "Duration must be at least 1 year")
    private Integer durationYear;

    @NotNull(message = "Total semesters is required")
    @Min(value = 1, message = "Total semesters must be at least 1")
    private Integer totalSemesters;

    @NotNull(message = "Department id is required")
    private Long departmentId;
}
