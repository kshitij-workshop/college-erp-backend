package com.kshitij.collegeerp.models.timetable.entity;

import com.kshitij.collegeerp.models.subject.entity.SubjectOffering;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "timetable_entries",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"day_of_week", "time_slot_id", "room_id"}),
                @UniqueConstraint(columnNames = {"day_of_week", "time_slot_id", "subject_offering_id"})
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TimetableEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_slot_id", nullable = false)
    private TimeSlot timeSlot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_offering_id", nullable = false)
    private SubjectOffering subjectOffering;

    @Column(nullable = false)
    private String academicSession;   // e.g. "2025-2026"
}