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
        throw new UnsupportedOperationException(
                "Faculty dashboard is not implemented yet."
        );
    }

    private DashboardResponse getStudentDashboard(User user) {
        throw new UnsupportedOperationException(
                "Student dashboard is not implemented yet."
        );
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