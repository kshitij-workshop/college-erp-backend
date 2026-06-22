package com.kshitij.collegeerp.models.notice.entity;

import com.kshitij.collegeerp.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tables")
@Getter @Setter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 5000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NoticeType noticeType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NoticeAudience audience;

    private Long departmentId;
    private Long sectionId;
    private Long programId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    private LocalDate expiryDate;

    @Column(nullable = false)
    private boolean active = true;
}
