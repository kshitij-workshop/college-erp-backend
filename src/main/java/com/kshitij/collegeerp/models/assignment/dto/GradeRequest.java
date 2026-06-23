package com.kshitij.collegeerp.models.assignment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GradeRequest {

    @NotNull(message = "Marks awarded is required")
    private Double marksAwarded;

    private String feedback;
}
