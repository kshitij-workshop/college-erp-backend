package com.kshitij.collegeerp.models.reports.dto;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class DepartmentSummaryResponse {
    private String departmentName;
    private String departmentCode;
    private long totalStudents;
    private long totalFaculty;
    private long totalPrograms;
    private long activeNotices;
}