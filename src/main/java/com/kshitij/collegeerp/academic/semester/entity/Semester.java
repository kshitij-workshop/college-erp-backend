package com.kshitij.collegeerp.academic.semester.entity;

import com.kshitij.collegeerp.academic.batch.entity.Batch;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "semesters")
@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class Semester {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer semesterNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean current = false; // is this the ongoing semester ?
}
