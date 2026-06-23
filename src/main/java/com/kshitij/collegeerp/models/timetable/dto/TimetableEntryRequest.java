package com.kshitij.collegeerp.models.timetable.dto;

import com.kshitij.collegeerp.models.timetable.entity.DayOfWeek;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TimetableEntryRequest {

    @NotNull(message = "Day of week is required")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Time slot ID is required")
    private Long timeSlotId;

    @NotNull(message = "Room ID is required")
    private Long roomId;

    @NotNull(message = "Subject offering ID is required")
    private Long subjectOfferingId;

    @NotBlank(message = "Academic session is required")
    private String academicSession;
}