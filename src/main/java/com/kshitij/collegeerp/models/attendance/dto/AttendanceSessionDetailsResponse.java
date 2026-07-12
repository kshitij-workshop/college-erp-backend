package com.kshitij.collegeerp.models.attendance.entity;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceSessionDetailsResponse {

    private Long sessionId;

    private LocalDate sessionDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private String subjectName;

    private String subjectCode;

    private String sectionName;

    private List<StudentAttendanceResponse> students;
}
