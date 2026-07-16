package com.kshitij.collegeerp.models.attendance.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentAttendanceDashboardResponse {

    private Double overallPercentage;

    private Integer presentClasses;

    private Integer totalClasses;

    private List<StudentSubjectAttendanceResponse> subjects;
}