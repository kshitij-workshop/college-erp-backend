package com.kshitij.collegeerp.models.timetable.dto;

import com.kshitij.collegeerp.models.timetable.entity.DayOfWeek;
import com.kshitij.collegeerp.models.timetable.entity.RoomType;
import lombok.*;
import java.time.LocalTime;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class TimetableEntryResponse {
    private Long id;
    private DayOfWeek dayOfWeek;
    private Long timeSlotId;
    private String timeSlotLabel;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long roomId;
    private String roomNumber;
    private RoomType roomType;
    private Long subjectOfferingId;
    private String subjectName;
    private String subjectCode;
    private String facultyName;
    private String sectionName;
    private String academicSession;
    private Long facultyId;
    private Long sectionId;

    private Long programId;
    private String programName;

    private Long semesterId;
    private Integer semesterNumber;
}
