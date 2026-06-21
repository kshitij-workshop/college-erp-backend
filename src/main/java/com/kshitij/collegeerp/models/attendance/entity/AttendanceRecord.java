package com.kshitij.collegeerp.models.attendance.entity;

import com.kshitij.collegeerp.models.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attendance_records", uniqueConstraints = @UniqueConstraint(columnNames = {"session_id", "student_id"}))
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private AttendanceSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;
}
