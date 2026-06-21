package com.kshitij.collegeerp.models.subject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SubjectOfferingRequest {

    @NotNull(message = "Subject id is required")
    private Long subjectId;

    @NotNull(message = "Section id is required")
    private Long sectionId;

    @NotNull(message = "Faculty id is required")
    private Long facultyId;

    @NotBlank(message = "Academic year is required")
    private String academicSession;
}
