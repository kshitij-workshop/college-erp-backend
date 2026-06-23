package com.kshitij.collegeerp.models.fee.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FeeStructureRequest {

    @NotNull(message = "Program ID is required")
    private Long programId;

    @NotNull(message = "Semester ID is required")
    private Long semesterId;

    @NotBlank(message = "Academic session is required")
    private String academicSession;

    @NotNull @Min(0)
    private Double tuitionFee;

    @NotNull @Min(0)
    private Double examFee;

    @NotNull @Min(0)
    private Double developmentFee;

    private Double hostelFee = 0.0;

    private Double otherFee = 0.0;
}