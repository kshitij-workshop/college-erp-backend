package com.kshitij.collegeerp.models.reports.controller;

import com.kshitij.collegeerp.common.response.ApiResponse;
import com.kshitij.collegeerp.models.reports.dto.*;
import com.kshitij.collegeerp.models.reports.service.ReportsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportsController {

    private final ReportsService reportsService;

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getAdminDashboard() {
        return ResponseEntity.ok(
                ApiResponse.success("Admin dashboard fetched successfully",
                        reportsService.getAdminDashboard()));
    }

    @GetMapping("/faculty/{facultyId}/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<ApiResponse<FacultyDashboardResponse>> getFacultyDashboard(
            @PathVariable Long facultyId) {
        return ResponseEntity.ok(
                ApiResponse.success("Faculty dashboard fetched successfully",
                        reportsService.getFacultyDashboard(facultyId)));
    }

    @GetMapping("/student/{studentId}/dashboard")
    public ResponseEntity<ApiResponse<StudentDashboardResponse>> getStudentDashboard(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(
                ApiResponse.success("Student dashboard fetched successfully",
                        reportsService.getStudentDashboard(studentId)));
    }

    @GetMapping("/department/{departmentId}/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOD')")
    public ResponseEntity<ApiResponse<DepartmentSummaryResponse>> getDepartmentSummary(
            @PathVariable Long departmentId) {
        return ResponseEntity.ok(
                ApiResponse.success("Department summary fetched successfully",
                        reportsService.getDepartmentSummary(departmentId)));
    }
}