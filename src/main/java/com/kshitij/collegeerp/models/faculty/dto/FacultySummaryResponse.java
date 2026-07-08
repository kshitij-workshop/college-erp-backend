package com.kshitij.collegeerp.models.faculty.dto;

import com.kshitij.collegeerp.models.faculty.entity.Designation;
import com.kshitij.collegeerp.models.faculty.entity.FacultyStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacultySummaryResponse {

    private Long id;

    // Identity

    private String employeeCode;

    // Personal Information

    private String fullName;

    private String email;

    private String phone;

    // Professional Information

    private Designation designation;

    private LocalDate joiningDate;

    // Academic Information

    private Long departmentId;

    private String departmentName;

    // Status

    private FacultyStatus status;

}