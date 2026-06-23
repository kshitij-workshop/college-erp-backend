package com.kshitij.collegeerp.models.fee.entity;

import com.kshitij.collegeerp.academic.program.entity.Program;
import com.kshitij.collegeerp.academic.semester.entity.Semester;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fee_structures")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeeStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @Column(nullable = false)
    private String academicSession;

    @Column(nullable = false)
    private Double examFee;

    @Column(nullable = false)
    private Double tuitionFee;

    @Column(nullable = false)
    private Double hostelFee;

    private Double developmentFee;
    private Double otherFee;

    private Double totalAmount; // auto calculated
}
