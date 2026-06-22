package com.kshitij.collegeerp.models.notice.repository;

import com.kshitij.collegeerp.models.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findByCreatedById(Long userId);

    // Active + unexpired notices
    @Query("""
        SELECT n FROM Notice n
        WHERE n.active = true
        AND (n.expiryDate IS NULL OR n.expiryDate >= :today)
        AND (
            n.audience = 'ALL'
            OR n.audience = 'STUDENTS'
            OR (n.audience = 'DEPARTMENT' AND n.departmentId = :departmentId)
            OR (n.audience = 'PROGRAM' AND n.programId = :programId)
            OR (n.audience = 'SECTION' AND n.sectionId = :sectionId)
        )
        ORDER BY n.createdAt DESC
    """)
    List<Notice> findRelevantNoticesForStudent(
            @Param("today") LocalDate today,
            @Param("departmentId") Long departmentId,
            @Param("programId") Long programId,
            @Param("sectionId") Long sectionId);

    @Query("""
        SELECT n FROM Notice n
        WHERE n.active = true
        AND (n.expiryDate IS NULL OR n.expiryDate >= :today)
        AND (n.audience = 'ALL' OR n.audience = 'FACULTY'
             OR (n.audience = 'DEPARTMENT' AND n.departmentId = :departmentId))
        ORDER BY n.createdAt DESC
    """)
    List<Notice> findRelevantNoticesForFaculty(
            @Param("today") LocalDate today,
            @Param("departmentId") Long departmentId);

    @Query("""
        SELECT n FROM Notice n
        WHERE n.active = true
        AND (n.expiryDate IS NULL OR n.expiryDate >= :today)
        ORDER BY n.createdAt DESC
    """)
    List<Notice> findAllActiveNotices(@Param("today") LocalDate today);
}
