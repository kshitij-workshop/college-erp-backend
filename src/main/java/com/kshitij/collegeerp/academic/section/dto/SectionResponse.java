package com.kshitij.collegeerp.academic.section.dto;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionResponse {
    private Long id;
    private String name;
    private Integer maxStrength;
    private Long semesterId;
    private Integer semesterNumber;
    private boolean active;
}
