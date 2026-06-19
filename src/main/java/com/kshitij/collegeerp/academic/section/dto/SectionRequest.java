package com.kshitij.collegeerp.academic.section.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SectionRequest {

    @NotBlank(message = "Section name is required")
    private String name;

    @NotNull(message = "Max strength is required")
    @Min(value = 1, message = "Mininum strength must be at least 1")
    private Integer maxStrength;

    @NotNull(message = "Semester id is required")
    private Long semesterId;
}
