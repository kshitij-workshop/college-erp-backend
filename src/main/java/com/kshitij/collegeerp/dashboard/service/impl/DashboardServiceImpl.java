package com.kshitij.collegeerp.dashboard.service.impl;

import com.kshitij.collegeerp.academic.department.repository.DepartmentRepository;
import com.kshitij.collegeerp.academic.program.repository.ProgramRepository;
import com.kshitij.collegeerp.auth.entity.Role;
import com.kshitij.collegeerp.auth.entity.User;
import com.kshitij.collegeerp.auth.repository.UserRepository;
import com.kshitij.collegeerp.common.exception.ResourceNotFoundException;
import com.kshitij.collegeerp.dashboard.dto.AdminDashboardResponse;
import com.kshitij.collegeerp.dashboard.dto.DashboardResponse;
import com.kshitij.collegeerp.dashboard.service.DashboardService;
import com.kshitij.collegeerp.models.assignment.repository.AssignmentRepository;
import com.kshitij.collegeerp.models.exam.repository.ExamRepository;
import com.kshitij.collegeerp.models.faculty.repository.FacultyRepository;
import com.kshitij.collegeerp.models.fee.repository.FeeInvoiceRepository;
import com.kshitij.collegeerp.models.library.repository.BookIssueRepository;
import com.kshitij.collegeerp.models.library.repository.BookRepository;
import com.kshitij.collegeerp.models.notice.repository.NoticeRepository;
import com.kshitij.collegeerp.models.student.repository.StudentRepository;
import com.kshitij.collegeerp.models.fee.entity.PaymentStatus;
import com.kshitij.collegeerp.models.subject.repository.SubjectOfferingRepository;
import com.kshitij.collegeerp.models.attendance.repository.AttendanceSessionRepository;
import com.kshitij.collegeerp.models.attendance.repository.AttendanceRecordRepository;
import com.kshitij.collegeerp.models.attendance.entity.AttendanceStatus;
import com.kshitij.collegeerp.models.assignment.repository.AssignmentSubmissionRepository;
import com.kshitij.collegeerp.models.exam.repository.MarksRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;

    private final DepartmentRepository departmentRepository;
    private final ProgramRepository programRepository;

    private final BookRepository bookRepository;
    private final BookIssueRepository bookIssueRepository;

    private final NoticeRepository noticeRepository;

    private final ExamRepository examRepository;

    private final AssignmentRepository assignmentRepository;

    private final FeeInvoiceRepository feeInvoiceRepository;

    private final SubjectOfferingRepository subjectOfferingRepository;
    private final AttendanceSessionRepository attendanceSessionRepository;

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final AssignmentSubmissionRepository submissionRepository;
    private final MarksRepository marksRepository;

    @Override
    public DashboardResponse getDashboard(Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return switch (user.getRole()) {

            case ADMIN -> getAdminDashboard();

            case FACULTY -> getFacultyDashboard(user);

            case STUDENT -> getStudentDashboard(user);

            case HOD -> getHodDashboard(user);

            case LIBRARIAN -> getLibrarianDashboard(user);

            case EXAM_CELL -> getExamCellDashboard(user);
        };
    }

    private DashboardResponse getFacultyDashboard(User user) {

        var faculty = facultyRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found"));

        long subjectsAssigned = subjectOfferingRepository.findByFacultyId(faculty.getId()).size();

        long sessionsTaken = attendanceSessionRepository
                .findBySubjectOfferingFacultyEmailOrderBySessionDateDescStartTimeDesc(faculty.getEmail())
                .size();

        long assignmentsCreated = assignmentRepository.findAll()
                .stream()
                .filter(a -> a.getSubjectOffering().getFaculty().getId().equals(faculty.getId()))
                .count();

        long examsCreated = examRepository.findAll()
                .stream()
                .filter(e -> e.getSubjectOffering().getFaculty().getId().equals(faculty.getId()))
                .count();

        return com.kshitij.collegeerp.dashboard.dto.FacultyDashboardResponse.builder()
                .facultyName(faculty.getFullName())
                .employeeCode(faculty.getEmployeeCode())
                .departmentName(faculty.getDepartment().getName())
                .totalSubjectsAssigned(subjectsAssigned)
                .totalAttendanceSessionsTaken(sessionsTaken)
                .totalAssignmentsCreated(assignmentsCreated)
                .totalExamsCreated(examsCreated)
                .build();

    }

    private DashboardResponse getStudentDashboard(User user) {

        var student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        // Attendance percentage
        long total = attendanceRecordRepository.countByStudentId(student.getId());
        long present = attendanceRecordRepository
                .countByStudentIdAndStatus(student.getId(), AttendanceStatus.PRESENT)
                + attendanceRecordRepository
                .countByStudentIdAndStatus(student.getId(), AttendanceStatus.LATE);
        double percentage = total == 0 ? 0.0 : (present * 100.0) / total;

        // Assignments submitted
        long assignmentsSubmitted = submissionRepository.findByStudentId(student.getId()).size();

        // Pending fee
        long pendingFee = feeInvoiceRepository
                .findByStudentId(student.getId())
                .stream()
                .filter(i -> i.getPaymentStatus() != PaymentStatus.PAID)
                .mapToLong(i -> i.getPendingAmount().longValue())
                .sum();

        // Books issued
        long booksIssued = bookIssueRepository
                .findByStudentIdAndStatus(student.getId(), com.kshitij.collegeerp.models.library.entity.IssueStatus.ISSUED)
                .size();

        // Exams appeared
        long examsAppeared = marksRepository.findByStudentId(student.getId()).size();

        return com.kshitij.collegeerp.dashboard.dto.StudentDashboardResponse.builder()
                .studentName(student.getFullName())
                .programName(student.getProgram().getName())
                .sectionName(student.getSection().getName())
                .semesterNumber(student.getSemester().getSemesterNumber())
                .rollNumber(student.getRollNumber())
                .registrationNumber(student.getRegistrationNumber())
                .overallAttendancePercentage(Math.round(percentage * 100.0) / 100.0)
                .totalAssignmentsSubmitted(assignmentsSubmitted)
                .pendingFeeAmount(pendingFee)
                .booksCurrentlyIssued(booksIssued)
                .totalExamsAppeared(examsAppeared)
                .build();

    }

    private DashboardResponse getHodDashboard(User user) {
        throw new UnsupportedOperationException(
                "HOD dashboard is not implemented yet."
        );
    }

    private DashboardResponse getLibrarianDashboard(User user) {
        throw new UnsupportedOperationException(
                "Librarian dashboard is not implemented yet."
        );
    }

    private DashboardResponse getExamCellDashboard(User user) {
        throw new UnsupportedOperationException(
                "Exam Cell dashboard is not implemented yet."
        );
    }

    private DashboardResponse getAdminDashboard() {

        return AdminDashboardResponse.builder()

                .totalStudents(studentRepository.count())

                .totalFaculty(facultyRepository.count())

                .totalDepartments(departmentRepository.count())

                .totalPrograms(programRepository.count())

                .totalBooks(bookRepository.count())

                .booksIssued(bookIssueRepository.count())

                .pendingFeeInvoices(
                        feeInvoiceRepository.countByPaymentStatus(PaymentStatus.PENDING)
                )

                .totalNotices(noticeRepository.count())

                .totalExams(examRepository.count())

                .totalAssignments(assignmentRepository.count())

                .build();
    }

}