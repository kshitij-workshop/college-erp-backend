package com.kshitij.collegeerp.models.exam.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class MarksEntryRequest {

    @NotNull(message = "Exam id is required")
    private Long examId;

    @NotEmpty(message = "Marks entries are required")
    private List<StudentMarksEntry> entries;


    @Getter @Setter
    public static class StudentMarksEntry{

        @NotNull(message = "Student id is required")
        private Long studentId;

        @NotNull(message = "Marks obtained is required")
        private Double marksObtained;
    }
}
