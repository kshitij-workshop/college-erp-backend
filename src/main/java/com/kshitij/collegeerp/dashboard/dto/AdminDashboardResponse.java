package com.kshitij.collegeerp.dashboard.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse implements DashboardResponse {

    private long totalStudents;
    private long totalFaculty;
    private long totalDepartments;
    private long totalPrograms;

    private long totalBooks;
    private long booksIssued;

    private long pendingFeeInvoices;

    private long totalNotices;

    private long totalExams;

    private long totalAssignments;

}