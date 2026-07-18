package com.kshitij.collegeerp.models.attendance.repository;

import com.kshitij.collegeerp.models.attendance.dto.AttendanceStudentHistoryResponse;
import com.kshitij.collegeerp.models.attendance.entity.AttendanceRecord;
import com.kshitij.collegeerp.models.attendance.entity.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
    List<AttendanceRecord> findBySessionId(Long sessionId);
    List<AttendanceRecord> findByStudentId(Long studentId);
    long countByStudentIdAndStatus(Long studentId, AttendanceStatus status);
    long countByStudentId(Long studentId);

    List<AttendanceRecord> findBySession_SubjectOffering_Id(Long subjectOfferingId);

    List<AttendanceRecord> findByStudent_IdAndSession_SubjectOffering_Id(
            Long studentId,
            Long subjectOfferingId
    );

    List<AttendanceRecord> findByStudent_Batch_Id(Long batchId);


}
