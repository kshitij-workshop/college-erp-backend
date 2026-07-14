package com.kshitij.collegeerp.models.attendance.dto;

import com.kshitij.collegeerp.models.attendance.entity.AttendanceStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAttendanceRequest {

    @NotEmpty(message = "Attendance entries are required.")
    private List<StudentAttendanceEntry> entries;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StudentAttendanceEntry {

        @NotNull
        private Long studentId;

        @NotNull
        private AttendanceStatus status;
    }
}