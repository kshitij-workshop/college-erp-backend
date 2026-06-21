package com.kshitij.collegeerp.models.subject.dto;

import com.kshitij.collegeerp.models.subject.entity.SubjectType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubjectRequest {

    @NotBlank(message = "Subject name is required")
    private String name;

    @NotBlank(message = "Subject code is required")
    private String code;

    @NotNull(message = "Credits are required")
    private Integer credits;

    @NotNull(message = "Subject type is required")
    private SubjectType type;

    @NotNull(message = "Program id is required")
    private Long programId;

    @NotNull(message = "Semester number is required")
    @Min(value = 1, message = "Semester number must be at least 1")
    @Max(value = 8, message = "Semester number can be greater than 8")
    private Integer semesterNumber;
}
