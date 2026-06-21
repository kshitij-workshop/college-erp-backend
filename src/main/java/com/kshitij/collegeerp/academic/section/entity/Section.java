package com.kshitij.collegeerp.academic.section.entity;

import com.kshitij.collegeerp.academic.semester.entity.Semester;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sections")
@Getter @Setter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // e.g. 'A'

    @Column(nullable = false)
    private Integer maxStrength; // e.g. 60

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @Column(nullable = false)
    private boolean active = true;

}
