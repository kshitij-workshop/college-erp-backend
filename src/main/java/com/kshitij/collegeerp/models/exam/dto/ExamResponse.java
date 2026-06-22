package com.kshitij.collegeerp.models.exam.dto;

import com.kshitij.collegeerp.models.exam.entity.ExamType;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExamResponse {
    private Long id;
    private String name;
    private ExamType examType;
    private Integer maxMarks;
    private Long subjectOfferingId;
    private String subjectName;
    private String sectionName;
    private LocalDate examDate;
    private boolean resultPublished;
    private boolean locked;
}
