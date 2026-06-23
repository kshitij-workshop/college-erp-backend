package com.kshitij.collegeerp.models.fee.dto;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class FeeStructureResponse {
    private Long id;
    private Long programId;
    private String programName;
    private Long semesterId;
    private Integer semesterNumber;
    private String academicSession;
    private Double tuitionFee;
    private Double examFee;
    private Double developmentFee;
    private Double hostelFee;
    private Double otherFee;
    private Double totalAmount;
}