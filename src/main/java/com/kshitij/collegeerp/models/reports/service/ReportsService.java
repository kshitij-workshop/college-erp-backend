package com.kshitij.collegeerp.models.reports.service;

import com.kshitij.collegeerp.academic.department.entity.Department;
import com.kshitij.collegeerp.academic.department.repository.DepartmentRepository;
import com.kshitij.collegeerp.models.assignment.repository.AssignmentRepository;
import com.kshitij.collegeerp.models.assignment.repository.AssignmentSubmissionRepository;
import com.kshitij.collegeerp.models.attendance.entity.AttendanceStatus;
import com.kshitij.collegeerp.models.attendance.repository.AttendanceRecordRepository;
import com.kshitij.collegeerp.models.attendance.repository.AttendanceSessionRepository;
import com.kshitij.collegeerp.common.exception.ResourceNotFoundException;
import com.kshitij.collegeerp.models.exam.repository.ExamRepository;
import com.kshitij.collegeerp.models.exam.repository.MarksRepository;
import com.kshitij.collegeerp.models.faculty.entity.Faculty;
import com.kshitij.collegeerp.models.faculty.repository.FacultyRepository;
import com.kshitij.collegeerp.models.fee.entity.PaymentStatus;
import com.kshitij.collegeerp.models.fee.repository.FeeInvoiceRepository;
import com.kshitij.collegeerp.models.library.entity.IssueStatus;
import com.kshitij.collegeerp.models.library.repository.BookIssueRepository;
import com.kshitij.collegeerp.models.library.repository.BookRepository;
import com.kshitij.collegeerp.models.notice.repository.NoticeRepository;
import com.kshitij.collegeerp.models.reports.dto.*;
import com.kshitij.collegeerp.models.student.entity.Student;
import com.kshitij.collegeerp.models.student.repository.StudentRepository;
import com.kshitij.collegeerp.models.subject.repository.SubjectOfferingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportsService {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final DepartmentRepository departmentRepository;
    private final BookRepository bookRepository;
    private final BookIssueRepository bookIssueRepository;
    private final FeeInvoiceRepository feeInvoiceRepository;
    private final NoticeRepository noticeRepository;
    private final ExamRepository examRepository;
    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository submissionRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final AttendanceSessionRepository attendanceSessionRepository;
    private final SubjectOfferingRepository subjectOfferingRepository;
    private final MarksRepository marksRepository;

    // ─── Admin Dashboard ──────────────────────────────────────

    public AdminDashboardResponse getAdminDashboard() {
        long pendingFees = feeInvoiceRepository
                .findByPaymentStatusIn(List.of(PaymentStatus.PENDING, PaymentStatus.PARTIAL))
                .size();

        return AdminDashboardResponse.builder()
                .totalStudents(studentRepository.count())
                .totalFaculty(facultyRepository.count())
                .totalDepartments(departmentRepository.count())
                .totalPrograms(0) // programRepository inject kar sakte ho if needed
                .totalBooks(bookRepository.count())
                .booksIssued(bookIssueRepository.findByStatus(IssueStatus.ISSUED).size())
                .pendingFeeInvoices(pendingFees)
                .totalNotices(noticeRepository.findAllActiveNotices(LocalDate.now()).size())
                .totalExams(examRepository.count())
                .totalAssignments(assignmentRepository.count())
                .build();
    }

    // ─── Faculty Dashboard ────────────────────────────────────

    public FacultyDashboardResponse getFacultyDashboard(Long facultyId) {
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found"));

        long subjectsAssigned = subjectOfferingRepository
                .findByFacultyId(facultyId).size();

        long sessionsTaken = attendanceSessionRepository
                .findAll()
                .stream()
                .filter(s -> s.getSubjectOffering().getFaculty().getId().equals(facultyId))
                .count();

        long assignmentsCreated = assignmentRepository
                .findAll()
                .stream()
                .filter(a -> a.getSubjectOffering().getFaculty().getId().equals(facultyId))
                .count();

        long examsCreated = examRepository
                .findAll()
                .stream()
                .filter(e -> e.getSubjectOffering().getFaculty().getId().equals(facultyId))
                .count();

        return FacultyDashboardResponse.builder()
                .facultyName(faculty.getFullName())
                .employeeCode(faculty.getEmployeeCode())
                .departmentName(faculty.getDepartment().getName())
                .totalSubjectsAssigned(subjectsAssigned)
                .totalAttendanceSessionsTaken(sessionsTaken)
                .totalAssignmentsCreated(assignmentsCreated)
                .totalExamsCreated(examsCreated)
                .build();
    }

    // ─── Student Dashboard ────────────────────────────────────

    public StudentDashboardResponse getStudentDashboard(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        // Attendance percentage
        long total = attendanceRecordRepository.countByStudentId(studentId);
        long present = attendanceRecordRepository
                .countByStudentIdAndStatus(studentId, AttendanceStatus.PRESENT)
                + attendanceRecordRepository
                .countByStudentIdAndStatus(studentId, AttendanceStatus.LATE);
        double percentage = total == 0 ? 0.0 : (present * 100.0) / total;

        // Assignments submitted
        long assignmentsSubmitted = submissionRepository
                .findByStudentId(studentId).size();

        // Pending fee
        long pendingFee = feeInvoiceRepository
                .findByStudentId(studentId)
                .stream()
                .filter(i -> i.getPaymentStatus() != PaymentStatus.PAID)
                .mapToLong(i -> i.getPendingAmount().longValue())
                .sum();

        // Books issued
        long booksIssued = bookIssueRepository
                .findByStudentIdAndStatus(studentId, IssueStatus.ISSUED).size();

        // Exams appeared
        long examsAppeared = marksRepository.findByStudentId(studentId).size();

        return StudentDashboardResponse.builder()
                .studentName(student.getFullName())
                .programName(student.getProgram().getName())
                .sectionName(student.getSection().getName())
                .semesterNumber(student.getSemester().getSemesterNumber())
                .overallAttendancePercentage(
                        Math.round(percentage * 100.0) / 100.0)
                .totalAssignmentsSubmitted(assignmentsSubmitted)
                .pendingFeeAmount(pendingFee)
                .booksCurrentlyIssued(booksIssued)
                .totalExamsAppeared(examsAppeared)
                .build();
    }

    // ─── Department Summary ───────────────────────────────────

    public DepartmentSummaryResponse getDepartmentSummary(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        long totalStudents = studentRepository
                .findByDepartmentId(departmentId).size();

        long totalFaculty = facultyRepository
                .findByDepartmentId(departmentId).size();

        long activeNotices = noticeRepository
                .findAllActiveNotices(LocalDate.now())
                .stream()
                .filter(n -> departmentId.equals(n.getDepartmentId())
                        || n.getDepartmentId() == null)
                .count();

        return DepartmentSummaryResponse.builder()
                .departmentName(department.getName())
                .departmentCode(department.getCode())
                .totalStudents(totalStudents)
                .totalFaculty(totalFaculty)
                .totalPrograms(0)
                .activeNotices(activeNotices)
                .build();
    }
}