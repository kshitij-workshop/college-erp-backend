// FacultyResponse.java
package com.kshitij.collegeerp.models.faculty.dto;

import com.kshitij.collegeerp.models.faculty.entity.Designation;
import com.kshitij.collegeerp.models.faculty.entity.FacultyStatus;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class FacultyResponse {
    private Long id;
    private String employeeCode;
    private String fullName;
    private String email;
    private String phone;
    private Designation designation;
    private String qualification;
    private String specialization;
    private Long departmentId;
    private String departmentName;
    private LocalDate joiningDate;
    private FacultyStatus status;
}