package com.kshitij.collegeerp.models.timetable.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rooms")
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    public String roomNumber; // "Room 101", "Lab 2"

    @Column(nullable = false)
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType roomType; // CLASSROOM, LAB, SEMINAR HALL

    @Column(nullable = false)
    private boolean active = true;
}
