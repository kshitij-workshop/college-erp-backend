package com.kshitij.collegeerp.models.subject.dto;

import com.kshitij.collegeerp.models.subject.entity.SubjectType;
import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class SubjectResponse {
    private Long id;
    private String name;
    private String code;
    private Integer credits;
    private SubjectType type;
    private Long programId;
    private String programName;
    private Integer semesterNumber;
    private boolean active;
}
