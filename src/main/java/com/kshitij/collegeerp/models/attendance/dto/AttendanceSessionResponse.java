package com.kshitij.collegeerp.models.attendance.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class AttendanceSessionResponse {
    private Long id;
    private Long subjectOfferingId;
    private String subjectName;
    private String sectionName;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean submitted;
    private List<StudentRecordResponse> records;

    @Getter @Setter @Builder
    @AllArgsConstructor @NoArgsConstructor
    public static class StudentRecordResponse{
        private Long studentId;
        private String studentName;
        private String enrollmentNumber;
        private String status;
    }
}
