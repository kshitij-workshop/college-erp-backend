package com.kshitij.collegeerp.models.student.dto;


import com.kshitij.collegeerp.models.student.entity.Gender;
import com.kshitij.collegeerp.models.student.entity.StudentStatus;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class StudentResponse {
    private Long id;
    private String enrollmentNumber;
    private String rollNumber;
    private String fullName;
    private String email;
    private String phone;
    private Gender gender;
    private LocalDate dateOfBirth;
    private String bloodGroup;
    private String address;
    private String guardianName;
    private String guardianPhone;
    private String guardianRelation;
    private String photoUrl;

    private Long departmentId;
    private String departmentName;
    private Long programId;
    private String programName;
    private Long batchId;
    private String batchName;
    private Long semesterId;
    private Integer semesterNumber;
    private Long sectionId;
    private String sectionName;

    private StudentStatus status;
    private LocalDate admissionDate;
}
