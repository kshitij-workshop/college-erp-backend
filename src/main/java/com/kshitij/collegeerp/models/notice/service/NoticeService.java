package com.kshitij.collegeerp.models.notice.service;

import com.kshitij.collegeerp.auth.entity.User;
import com.kshitij.collegeerp.auth.repository.UserRepository;
import com.kshitij.collegeerp.common.exception.ResourceNotFoundException;
import com.kshitij.collegeerp.models.notice.dto.NoticeRequest;
import com.kshitij.collegeerp.models.notice.dto.NoticeResponse;
import com.kshitij.collegeerp.models.notice.entity.Notice;
import com.kshitij.collegeerp.models.notice.entity.NoticeAudience;
import com.kshitij.collegeerp.models.notice.repository.NoticeRepository;
import com.kshitij.collegeerp.models.student.entity.Student;
import com.kshitij.collegeerp.models.student.repository.StudentRepository;
import com.kshitij.collegeerp.models.faculty.entity.Faculty;
import com.kshitij.collegeerp.models.faculty.repository.FacultyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;

    @Transactional
    public NoticeResponse create(NoticeRequest request) {

        // Audience validation
        if (request.getAudience() == NoticeAudience.DEPARTMENT
                && request.getDepartmentId() == null) {
            throw new RuntimeException("Department ID is required for DEPARTMENT audience");
        }
        if (request.getAudience() == NoticeAudience.PROGRAM
                && request.getProgramId() == null) {
            throw new RuntimeException("Program ID is required for PROGRAM audience");
        }
        if (request.getAudience() == NoticeAudience.SECTION
                && request.getSectionId() == null) {
            throw new RuntimeException("Section ID is required for SECTION audience");
        }

        // Expiry date validation
        if (request.getExpiryDate() != null
                && request.getExpiryDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Expiry date cannot be in the past");
        }

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User createdBy = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Notice notice = Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .noticeType(request.getNoticeType())
                .audience(request.getAudience())
                .departmentId(request.getDepartmentId())
                .programId(request.getProgramId())
                .sectionId(request.getSectionId())
                .createdBy(createdBy)
                .createdAt(LocalDateTime.now())
                .expiryDate(request.getExpiryDate())
                .active(true)
                .build();

        Notice saved = noticeRepository.save(notice);
        return mapToResponse(saved);
    }

    public List<NoticeResponse> getAll() {
        return noticeRepository.findAllActiveNotices(LocalDate.now())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Notices for students
    public List<NoticeResponse> getMyNoticesAsStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        return noticeRepository.findRelevantNoticesForStudent(
                        LocalDate.now(),
                        student.getDepartment().getId(),
                        student.getProgram().getId(),
                        student.getSection().getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Relevant notices for faculty
    public List<NoticeResponse> getMyNoticesAsFaculty(Long facultyId) {
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found"));

        return noticeRepository.findRelevantNoticesForFaculty(
                        LocalDate.now(),
                        faculty.getDepartment().getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public NoticeResponse getById(Long id) {
        return mapToResponse(findNoticeById(id));
    }

    @Transactional
    public NoticeResponse update(Long id, NoticeRequest request) {
        Notice notice = findNoticeById(id);

        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());
        notice.setNoticeType(request.getNoticeType());
        notice.setAudience(request.getAudience());
        notice.setDepartmentId(request.getDepartmentId());
        notice.setProgramId(request.getProgramId());
        notice.setSectionId(request.getSectionId());
        notice.setExpiryDate(request.getExpiryDate());

        return mapToResponse(noticeRepository.save(notice));
    }

    @Transactional
    public void deactivate(Long id) {
        Notice notice = findNoticeById(id);
        notice.setActive(false);
        noticeRepository.save(notice);
    }

    @Transactional
    public void delete(Long id) {
        noticeRepository.delete(findNoticeById(id));
    }

    private Notice findNoticeById(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notice not found with id: " + id));
    }

    private NoticeResponse mapToResponse(Notice notice) {
        return NoticeResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .noticeType(notice.getNoticeType())
                .audience(notice.getAudience())
                .departmentId(notice.getDepartmentId())
                .programId(notice.getProgramId())
                .sectionId(notice.getSectionId())
                .createdByName(notice.getCreatedBy().getFullName())
                .createdAt(notice.getCreatedAt())
                .expiryDate(notice.getExpiryDate())
                .active(notice.isActive())
                .build();
    }
}