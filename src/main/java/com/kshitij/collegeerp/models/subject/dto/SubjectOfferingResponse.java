package com.kshitij.collegeerp.models.subject.dto;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class SubjectOfferingResponse {
    private Long id;
    private Long subjectId;
    private String subjectName;
    private String subjectCode;
    private Long sectionId;
    private String sectionName;
    private Long facultyId;
    private String facultyName;
    private String academicSession;
    private String batchName;
    private String departmentCode;
    private Long programId;
    private String programName;
    private Integer semesterNumber;
    private boolean active;
}
