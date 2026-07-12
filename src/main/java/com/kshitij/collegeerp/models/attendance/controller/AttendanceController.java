package com.kshitij.collegeerp.models.attendance.controller;

import com.kshitij.collegeerp.models.attendance.dto.*;
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

    @GetMapping("/faculty/me/classes")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<ApiResponse<List<FacultyClassResponse>>> getMyClasses(
            @RequestParam LocalDate date
    ) {

        return ResponseEntity.ok(

                ApiResponse.success(

                        "Classes fetched successfully.",

                        attendanceService.getMyClasses(date)
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

    @GetMapping("/session/{sessionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'HOD')")
    public ResponseEntity<ApiResponse<AttendanceSessionResponse>> getSession(
            @PathVariable Long sessionId) {
        return ResponseEntity.ok(
                ApiResponse.success("Session fetched successfully",
                        attendanceService.getSessionById(sessionId)));
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
}