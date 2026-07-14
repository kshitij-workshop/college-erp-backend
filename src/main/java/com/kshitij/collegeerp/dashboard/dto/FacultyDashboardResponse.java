package com.kshitij.collegeerp.dashboard.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacultyDashboardResponse implements DashboardResponse {
    private String facultyName;
    private String employeeCode;
    private String departmentName;
    private long totalSubjectsAssigned;
    private long totalAttendanceSessionsTaken;
    private long totalAssignmentsCreated;
    private long totalExamsCreated;
}