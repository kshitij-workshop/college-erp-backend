package com.kshitij.collegeerp.models.attendance.dto;

import com.kshitij.collegeerp.models.attendance.entity.AttendanceStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceStudentHistoryResponse {
    private Long sessionId;

    private LocalDate sessionDate;

    private String subjectName;

    private String subjectCode;

    private String sectionName;

    private LocalTime startTime;

    private LocalTime endTime;

    private AttendanceStatus status;
}
