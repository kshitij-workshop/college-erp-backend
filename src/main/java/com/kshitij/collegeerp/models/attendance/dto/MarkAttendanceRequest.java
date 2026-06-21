package com.kshitij.collegeerp.models.attendance.dto;

import com.kshitij.collegeerp.models.attendance.entity.AttendanceStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter @Setter
public class MarkAttendanceRequest {

    @NotNull(message = "Subject offering ID is required")
    private Long subjectOfferingId;

    @NotNull(message = "Session date is required")
    private LocalDate sessionDate;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @NotEmpty(message = "Attendance entries are required")
    private List<StudentAttendanceEntry> entries;

    @Getter @Setter
    public static class StudentAttendanceEntry{

        @NotNull(message = "Student ID is required")
        private Long studentId;

        @NotNull(message = "Attendance status is required")
        private AttendanceStatus status;
    }
}
