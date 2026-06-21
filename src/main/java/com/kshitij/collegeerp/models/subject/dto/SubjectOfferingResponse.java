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
    private boolean active;
}
