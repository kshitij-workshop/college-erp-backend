package com.kshitij.collegeerp.academic.batch.entity;

import com.kshitij.collegeerp.academic.program.entity.Program;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "batches")
@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // e.g. 2024-28

    @Column(nullable = false)
    private Integer startYear;

    @Column(nullable = false)
    private Integer endYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @Column(nullable = false)
    private boolean active = true;
}
