package com.kshitij.collegeerp.academic.batch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BatchRequest {

    @NotBlank(message = "Batch name is required")
    private String name;

    @NotNull(message = "Start year is required")
    private Integer startYear;

    @NotNull(message = "End year is required")
    private Integer endYear;

    @NotNull(message = "Program id is required")
    private Long programId;
}
