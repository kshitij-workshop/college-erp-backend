package com.kshitij.collegeerp.academic.program.dto;


import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ProgramResponse {

    private Long id;
    private String name;
    private String code;
    private Integer durationYear;
    private Integer totalSemester;
    private Long departmentId;
    private String departmentName;
    private boolean active;
}
