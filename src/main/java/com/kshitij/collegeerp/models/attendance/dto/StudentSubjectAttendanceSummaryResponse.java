package com.kshitij.collegeerp.models.attendance.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentSubjectAttendanceSummaryResponse {

    private Long subjectOfferingId;

    private String subjectCode;

    private String subjectName;

    private Integer presentClasses;

    private Integer totalClasses;

    private Double percentage;

}