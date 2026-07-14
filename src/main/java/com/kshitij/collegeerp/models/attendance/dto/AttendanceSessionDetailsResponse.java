package com.kshitij.collegeerp.models.attendance.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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

    private List<AttendanceStudentResponse> students;
}
