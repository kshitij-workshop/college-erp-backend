package com.kshitij.collegeerp.models.attendance.dto;

import com.kshitij.collegeerp.models.attendance.entity.AttendanceStatus;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceStudentResponse {

    private Long studentId;

    private String rollNumber;

    private Long registrationNumber;

    private String fullName;

    private AttendanceStatus status;
}