package com.kshitij.collegeerp.models.attendance.dto;

import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class AttendancePercentageResponse {
    private Long studentId;
    private String studentName;
    private long totalClasses;
    private long presentCount;
    private double percentage;
}
