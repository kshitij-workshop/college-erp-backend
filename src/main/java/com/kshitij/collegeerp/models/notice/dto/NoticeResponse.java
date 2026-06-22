package com.kshitij.collegeerp.models.notice.dto;

import com.kshitij.collegeerp.models.notice.entity.NoticeAudience;
import com.kshitij.collegeerp.models.notice.entity.NoticeType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class NoticeResponse {
    private Long id;
    private String title;
    private String content;
    private NoticeType noticeType;
    private NoticeAudience audience;
    private Long departmentId;
    private Long programId;
    private Long sectionId;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDate expiryDate;
    private boolean active;
}