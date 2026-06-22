package com.kshitij.collegeerp.models.exam.entity;

import com.kshitij.collegeerp.models.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "marks", uniqueConstraints = @UniqueConstraint(columnNames = {"exam_id", "student_id"}))
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Marks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    private Double marksObtained;
}
