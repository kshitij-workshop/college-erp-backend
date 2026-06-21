package com.kshitij.collegeerp.models.attendance.entity;

import com.kshitij.collegeerp.models.subject.entity.SubjectOffering;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "attendance_sessions", uniqueConstraints = @UniqueConstraint(columnNames = {"subject_offering_id", "session_date", "start_time"}))
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class AttendanceSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_offering_id", nullable = false)
    private SubjectOffering subjectOffering;

    @Column(nullable = false)
    private LocalDate sessionDate;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private boolean submitted = false;

}
