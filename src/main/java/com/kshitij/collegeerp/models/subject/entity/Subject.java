package com.kshitij.collegeerp.models.subject.entity;

import com.kshitij.collegeerp.academic.program.entity.Program;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subjects")
@Getter @Setter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private Integer credits;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubjectType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @Column(nullable = false)
    private Integer semesterNumber;

    @Column(nullable = false)
    private boolean active = true;

}
