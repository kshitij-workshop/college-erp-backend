package com.kshitij.collegeerp.models.attendance.dto;

import com.kshitij.collegeerp.models.attendance.entity.AttendanceStatus;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentAttendanceHistoryItemResponse {

    private Long attendanceRecordId;

    private LocalDate attendanceDate;

    private AttendanceStatus status;
}