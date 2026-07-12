package com.kshitij.collegeerp.models.attendance.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceStudentResponse {

    private Long studentId;

    private Long registrationNumber;

    private String fullName;
}