package com.kshitij.collegeerp.models.attendance.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentSubjectAttendanceDetailsResponse {

    private Long studentId;

    private String studentName;

    private Long registrationNumber;

    private String subjectCode;

    private String subjectName;

    private String facultyName;

    private Integer presentClasses;

    private Integer totalClasses;

    private Double percentage;

    private List<StudentAttendanceHistoryItemResponse> attendanceHistory;

}