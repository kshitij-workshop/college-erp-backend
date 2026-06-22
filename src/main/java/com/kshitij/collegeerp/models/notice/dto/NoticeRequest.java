package com.kshitij.collegeerp.models.notice.dto;

import com.kshitij.collegeerp.models.notice.entity.NoticeAudience;
import com.kshitij.collegeerp.models.notice.entity.NoticeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class NoticeRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "Notice type is required")
    private NoticeType noticeType;

    @NotNull(message = "Audience is required")
    private NoticeAudience audience;

    private Long departmentId;
    private Long programId;
    private Long sectionId;

    private LocalDate expiryDate;
}