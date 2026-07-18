package com.kshitij.collegeerp.models.attendance.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentOverallAttendanceResponse {

    private Long studentId;

    private String studentName;

    private Long registrationNumber;

    private Integer presentClasses;

    private Integer totalClasses;

    private Double overallPercentage;

    private List<StudentSubjectAttendanceSummaryResponse> subjects;

}