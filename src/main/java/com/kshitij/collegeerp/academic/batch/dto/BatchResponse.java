package com.kshitij.collegeerp.academic.batch.dto;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchResponse {
    private Long id;
    private String name;
    private Integer startYear;
    private Integer endYear;
    private Long programId;
    private String programName;
    private boolean active;
    private Long departmentId;
    private String departmentName;
}
