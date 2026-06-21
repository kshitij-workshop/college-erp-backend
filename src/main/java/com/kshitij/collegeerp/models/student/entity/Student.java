package com.kshitij.collegeerp.models.student.entity;


import com.kshitij.collegeerp.academic.batch.entity.Batch;
import com.kshitij.collegeerp.academic.department.entity.Department;
import com.kshitij.collegeerp.academic.program.entity.Program;
import com.kshitij.collegeerp.academic.section.entity.Section;
import com.kshitij.collegeerp.academic.semester.entity.Semester;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "students")
@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identity

    @Column(nullable = false, unique = true)
    private String enrollmentNumber;

    @Column(unique = true, nullable = true)
    private String rollNumber;

    // Personal Info

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

    // Guardian info
    private String guardianName;
    private String guardianPhone;
    private String guardianRelation;

    // photo
    private String photoUrl;

    // Academic Structure
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;


    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudentStatus status;

    private LocalDate admissionDate;





}
