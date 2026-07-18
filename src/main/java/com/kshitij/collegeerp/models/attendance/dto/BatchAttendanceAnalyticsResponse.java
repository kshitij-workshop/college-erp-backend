package com.kshitij.collegeerp.models.attendance.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchAttendanceAnalyticsResponse {

    private Long studentId;

    private String studentName;

    private Long registrationNumber;

    private Integer presentClasses;

    private Integer totalClasses;

    private Double percentage;

}