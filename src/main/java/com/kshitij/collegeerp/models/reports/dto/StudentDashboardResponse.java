package com.kshitij.collegeerp.models.reports.dto;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class StudentDashboardResponse implements DashboardResponse{
    private String studentName;
    private String enrollmentNumber;
    private String programName;
    private String sectionName;
    private Integer semesterNumber;
    private double overallAttendancePercentage;
    private long totalAssignmentsSubmitted;
    private long pendingFeeAmount;
    private long booksCurrentlyIssued;
    private long totalExamsAppeared;
}