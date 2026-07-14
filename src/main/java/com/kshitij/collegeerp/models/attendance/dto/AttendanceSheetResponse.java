package com.kshitij.collegeerp.models.attendance.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceSheetResponse {

    private boolean attendanceMarked;

    private Long sessionId;

    private Long timetableEntryId;

    private String subjectName;

    private String sectionName;

    private LocalDate sessionDate;

    private List<AttendanceStudentResponse> students;
}