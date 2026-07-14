package com.kshitij.collegeerp.models.attendance.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceHistoryResponse {

    private Long sessionId;

    private LocalDate sessionDate;

    private String subjectName;

    private String subjectCode;

    private String sectionName;

    private LocalTime startTime;

    private LocalTime endTime;

    private long presentCount;

    private long absentCount;

    private long lateCount;

    private long leaveCount;
}