package com.kshitij.collegeerp.models.exam.entity;

import com.kshitij.collegeerp.models.subject.entity.SubjectOffering;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "exams")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // "Class test 1"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExamType examType;

    @Column(nullable = false)
    private Integer maxMarks;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_offering_id", nullable = false)
    private SubjectOffering subjectOffering;

    private LocalDate examDate;

    @Column(nullable = false)
    private boolean resultPublished = false;

    @Column(nullable = false)
    private boolean locked = false; // marks locked after validation

}
