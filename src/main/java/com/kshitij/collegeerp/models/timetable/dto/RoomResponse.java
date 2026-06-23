package com.kshitij.collegeerp.models.timetable.dto;

import com.kshitij.collegeerp.models.timetable.entity.RoomType;
import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class RoomResponse {
    private Long id;
    private String roomNumber;
    private Integer capacity;
    private RoomType roomType;
    private boolean active;
}