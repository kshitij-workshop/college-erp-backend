package com.kshitij.collegeerp.dashboard.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDashboardResponse implements DashboardResponse {
    private String studentName;
    private String programName;
    private String sectionName;
    private int semesterNumber;
    private Long registrationNumber;
    private String rollNumber;
    private double overallAttendancePercentage;
    private long totalAssignmentsSubmitted;
    private long pendingFeeAmount;
    private long booksCurrentlyIssued;
    private long totalExamsAppeared;
}