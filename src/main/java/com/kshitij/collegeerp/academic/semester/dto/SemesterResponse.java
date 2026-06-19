package com.kshitij.collegeerp.academic.semester.dto;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class SemesterResponse {
    private Long id;
    private Integer semesterNumber;
    private Long batchId;
    private String batchName;
    private boolean active;
    private boolean current;

}
