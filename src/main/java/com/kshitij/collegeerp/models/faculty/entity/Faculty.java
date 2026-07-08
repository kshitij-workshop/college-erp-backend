package com.kshitij.collegeerp.models.faculty.entity;

import com.kshitij.collegeerp.academic.department.entity.Department;
import com.kshitij.collegeerp.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "faculties")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    // Identity

    @Column(nullable = false, unique = true)
    private String employeeCode;

    // Personal Information

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate dateOfBirth;

    private String bloodGroup;

    @Column(length = 500)
    private String address;

    // Professional Information

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Designation designation;

    @Column(nullable = false)
    private String qualification;

    private String specialization;

    private Integer experienceYears;

    private LocalDate joiningDate;

    private String photoUrl;

    // Academic Structure

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    // Status

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FacultyStatus status;

}