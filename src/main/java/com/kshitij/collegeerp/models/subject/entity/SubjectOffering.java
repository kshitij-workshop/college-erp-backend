package com.kshitij.collegeerp.models.subject.entity;

import com.kshitij.collegeerp.academic.section.entity.Section;
import com.kshitij.collegeerp.models.faculty.entity.Faculty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subject_offerings")
@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectOffering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    @Column(nullable = false)
    private String academicSession;

    @Column(nullable = false)
    private boolean active = true;
}
