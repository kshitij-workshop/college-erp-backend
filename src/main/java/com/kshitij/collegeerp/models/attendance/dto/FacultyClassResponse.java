package com.kshitij.collegeerp.models.attendance.dto;

import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FacultyClassResponse {

    private Long timetableEntryId;

    private Long subjectOfferingId;

    private String facultyName;

    private String subjectName;

    private String subjectCode;

    private String sectionName;

    private String roomNumber;

    private LocalTime startTime;

    private LocalTime endTime;

    private boolean attendanceMarked;
}