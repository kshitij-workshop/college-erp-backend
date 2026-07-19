package com.kshitij.collegeerp.models.attendance.controller;

import com.kshitij.collegeerp.models.attendance.dto.*;
import com.kshitij.collegeerp.models.attendance.entity.AttendanceRecord;
import com.kshitij.collegeerp.models.attendance.service.AttendanceService;
import com.kshitij.collegeerp.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/mark")
    @PreAuthorize("hasAnyRole('ADMIN','FACULTY')")
    public ResponseEntity<ApiResponse<AttendanceSessionResponse>> markAttendance(
            @Valid @RequestBody MarkAttendanceRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Attendance marked successfully",
                        attendanceService.markAttendance(request)));
    }

    @GetMapping("/classes")
    @PreAuthorize("hasAnyRole('ADMIN','FACULTY')")
    public ResponseEntity<ApiResponse<List<FacultyClassResponse>>> getClasses(
            @RequestParam LocalDate date
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Classes fetched successfully.",
                        attendanceService.getClasses(date)
                )
        );
    }

    @GetMapping("/classes/{timetableEntryId}/sheet")
    @PreAuthorize("hasAnyRole('ADMIN','FACULTY')")
    public ResponseEntity<ApiResponse<AttendanceSheetResponse>>
    getAttendanceSheet(
            @PathVariable Long timetableEntryId,
            @RequestParam LocalDate date
    ) {

        return ResponseEntity.ok(

                ApiResponse.success(

                        "Attendance sheet fetched successfully.",

                        attendanceService.getAttendanceSheet(
                                timetableEntryId,
                                date
                        )
                )
        );
    }

    @PutMapping("/session/{sessionId}")
    @PreAuthorize("hasAnyRole('ADMIN','FACULTY')")
    public ResponseEntity<ApiResponse<AttendanceSessionResponse>> updateAttendance(
            @PathVariable Long sessionId,
            @Valid @RequestBody UpdateAttendanceRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Attendance updated successfully.",
                        attendanceService.updateAttendance(sessionId, request)
                )
        );
    }

    @GetMapping("/classes/{timetableEntryId}/students")
    @PreAuthorize("hasAnyRole('ADMIN','FACULTY')")
    public ResponseEntity<ApiResponse<List<AttendanceStudentResponse>>> getStudentsForClass(
            @PathVariable Long timetableEntryId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Students fetched successfully.",
                        attendanceService.getStudentsForClass(timetableEntryId)
                )
        );
    }

    @GetMapping("/offering/{subjectOfferingId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'HOD')")
    public ResponseEntity<ApiResponse<List<AttendanceSessionResponse>>> getSessionsByOffering(
            @PathVariable Long subjectOfferingId) {
        return ResponseEntity.ok(
                ApiResponse.success("Sessions fetched successfully",
                        attendanceService.getSessionsByOffering(subjectOfferingId)));
    }

    @GetMapping("/student/{studentId}/percentage")
    public ResponseEntity<ApiResponse<AttendancePercentageResponse>> getStudentPercentage(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(
                ApiResponse.success("Attendance percentage calculated successfully",
                        attendanceService.getStudentPercentage(studentId)));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StudentOverallAttendanceResponse>> getStudentOverallAttendance(@PathVariable Long studentId) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Student attendance fetched successfully",
                        attendanceService.getStudentOverallAttendance(studentId)
                )
        );

    }


    @GetMapping("/my/dashboard")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<StudentAttendanceDashboardResponse>> getMyAttendanceDashboard() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Attendance dashboard fetched successfully",
                        attendanceService.getStudentDashboard()
                )
        );
    }

    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('ADMIN','FACULTY')")
    public ResponseEntity<ApiResponse<List<AttendanceHistoryResponse>>>
    getAttendanceHistory() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Attendance history fetched successfully.",
                        attendanceService.getAttendanceHistory()
                )
        );
    }

    @GetMapping("/analytics/{subjectOfferingId}")
    @PreAuthorize("hasAnyRole('ADMIN','FACULTY')")
    public ResponseEntity<ApiResponse<List<StudentAttendanceAnalyticsResponse>>> getAttendanceAnalytics(
            @PathVariable Long subjectOfferingId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Attendance analytics fetched successfully",
                        attendanceService.getAttendanceAnalytics(subjectOfferingId)
                )
        );
    }

    @GetMapping("/student/{studentId}/subject/{subjectOfferingId}")
    @PreAuthorize("hasAnyRole('ADMIN','FACULTY')")
    public ResponseEntity<ApiResponse<StudentSubjectAttendanceDetailsResponse>>
    getStudentSubjectAttendance(
            @PathVariable Long studentId,
            @PathVariable Long subjectOfferingId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Student attendance fetched successfully",
                        attendanceService.getStudentSubjectAttendance(
                                studentId,
                                subjectOfferingId
                        )
                )
        );
    }

    @GetMapping("/analytics/batch/{batchId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<ApiResponse<List<BatchAttendanceAnalyticsResponse>>>
    getBatchAttendanceAnalytics(
            @PathVariable Long batchId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Batch attendance analytics fetched successfully",
                        attendanceService.getBatchAttendanceAnalytics(batchId)
                )
        );

    }

    @GetMapping("/session/{sessionId}")
    @PreAuthorize("hasAnyRole('ADMIN','FACULTY')")
    public ResponseEntity<ApiResponse<AttendanceSessionDetailsResponse>>
    getAttendanceSession(
            @PathVariable Long sessionId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Attendance session fetched successfully.",
                        attendanceService.getAttendanceSession(sessionId)
                )
        );
    }
}