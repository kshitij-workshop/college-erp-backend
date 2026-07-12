package com.kshitij.collegeerp.models.fee.service;

import com.kshitij.collegeerp.academic.program.entity.Program;
import com.kshitij.collegeerp.academic.program.repository.ProgramRepository;
import com.kshitij.collegeerp.academic.semester.entity.Semester;
import com.kshitij.collegeerp.academic.semester.repository.SemesterRepository;
import com.kshitij.collegeerp.common.exception.ResourceNotFoundException;
import com.kshitij.collegeerp.models.fee.dto.*;
import com.kshitij.collegeerp.models.fee.entity.*;
import com.kshitij.collegeerp.models.fee.repository.*;
import com.kshitij.collegeerp.models.student.entity.Student;
import com.kshitij.collegeerp.models.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeeService {

    private final FeeStructureRepository feeStructureRepository;
    private final FeeInvoiceRepository feeInvoiceRepository;
    private final FeePaymentRepository feePaymentRepository;
    private final StudentRepository studentRepository;
    private final ProgramRepository programRepository;
    private final SemesterRepository semesterRepository;

    // ─── Fee Structure ────────────────────────────────────────

    @Transactional
    public FeeStructureResponse createFeeStructure(FeeStructureRequest request) {
        if (feeStructureRepository.findByProgramIdAndSemesterIdAndAcademicSession(
                request.getProgramId(), request.getSemesterId(),
                request.getAcademicSession()).isPresent()) {
            throw new RuntimeException(
                    "Fee structure already exists for this program, semester and session");
        }

        Program program = programRepository.findById(request.getProgramId())
                .orElseThrow(() -> new ResourceNotFoundException("Program not found"));
        Semester semester = semesterRepository.findById(request.getSemesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Semester not found"));

        double total = request.getTuitionFee()
                + request.getExamFee()
                + request.getDevelopmentFee()
                + (request.getHostelFee() != null ? request.getHostelFee() : 0)
                + (request.getOtherFee() != null ? request.getOtherFee() : 0);

        FeeStructure structure = FeeStructure.builder()
                .program(program)
                .semester(semester)
                .academicSession(request.getAcademicSession())
                .tuitionFee(request.getTuitionFee())
                .examFee(request.getExamFee())
                .developmentFee(request.getDevelopmentFee())
                .hostelFee(request.getHostelFee() != null ? request.getHostelFee() : 0)
                .otherFee(request.getOtherFee() != null ? request.getOtherFee() : 0)
                .totalAmount(total)
                .build();

        return mapStructureToResponse(feeStructureRepository.save(structure));
    }

    public List<FeeStructureResponse> getAllFeeStructures() {
        return feeStructureRepository.findAll()
                .stream()
                .map(this::mapStructureToResponse)
                .toList();
    }

    public List<FeeStructureResponse> getFeeStructuresByProgram(Long programId) {
        return feeStructureRepository.findByProgramId(programId)
                .stream()
                .map(this::mapStructureToResponse)
                .toList();
    }

    // ─── Invoice ──────────────────────────────────────────────

    @Transactional
    public FeeInvoiceResponse generateInvoice(Long studentId, Long feeStructureId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        FeeStructure structure = feeStructureRepository.findById(feeStructureId)
                .orElseThrow(() -> new ResourceNotFoundException("Fee structure not found"));

        if (feeInvoiceRepository.existsByStudentIdAndFeeStructureId(studentId, feeStructureId)) {
            throw new RuntimeException(
                    "Invoice already generated for this student and fee structure");
        }

        if (!student.getProgram().getId().equals(structure.getProgram().getId())) {
            throw new RuntimeException(
                    "Fee structure does not match student's program");
        }

        String invoiceNumber = "INV-" + System.currentTimeMillis();

        FeeInvoice invoice = FeeInvoice.builder()
                .invoiceNumber(invoiceNumber)
                .student(student)
                .feeStructure(structure)
                .totalAmount(structure.getTotalAmount())
                .paidAmount(0.0)
                .pendingAmount(structure.getTotalAmount())
                .paymentStatus(PaymentStatus.PENDING)
                .invoiceDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(30))
                .build();

        return mapInvoiceToResponse(feeInvoiceRepository.save(invoice));
    }

    public List<FeeInvoiceResponse> getInvoicesByStudent(Long studentId) {
        return feeInvoiceRepository.findByStudentId(studentId)
                .stream()
                .map(this::mapInvoiceToResponse)
                .toList();
    }

    public List<FeeInvoiceResponse> getPendingInvoices() {
        return feeInvoiceRepository
                .findByPaymentStatusIn(
                        List.of(PaymentStatus.PENDING, PaymentStatus.PARTIAL))
                .stream()
                .map(this::mapInvoiceToResponse)
                .toList();
    }

    // ─── Payment ──────────────────────────────────────────────

    @Transactional
    public PaymentResponse recordPayment(PaymentRequest request) {
        FeeInvoice invoice = feeInvoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

        if (invoice.getPaymentStatus() == PaymentStatus.PAID) {
            throw new RuntimeException("Invoice already fully paid");
        }

        if (request.getAmountPaid() > invoice.getPendingAmount()) {
            throw new RuntimeException(
                    "Amount paid (" + request.getAmountPaid() +
                            ") cannot exceed pending amount (" + invoice.getPendingAmount() + ")");
        }

        String receiptNumber = "RCP-" + System.currentTimeMillis();

        FeePayment payment = FeePayment.builder()
                .receiptNumber(receiptNumber)
                .invoice(invoice)
                .amountPaid(request.getAmountPaid())
                .paymentMode(request.getPaymentMode())
                .paymentDate(LocalDateTime.now())
                .transactionId(request.getTransactionId())
                .build();

        feePaymentRepository.save(payment);

        double newPaidAmount = invoice.getPaidAmount() + request.getAmountPaid();
        double newPendingAmount = invoice.getTotalAmount() - newPaidAmount;

        invoice.setPaidAmount(newPaidAmount);
        invoice.setPendingAmount(newPendingAmount);

        if (newPendingAmount <= 0) {
            invoice.setPaymentStatus(PaymentStatus.PAID);
        } else {
            invoice.setPaymentStatus(PaymentStatus.PARTIAL);
        }

        feeInvoiceRepository.save(invoice);

        return PaymentResponse.builder()
                .id(payment.getId())
                .receiptNumber(payment.getReceiptNumber())
                .invoiceId(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .studentName(invoice.getStudent().getFullName())
                .amountPaid(payment.getAmountPaid())
                .paymentMode(payment.getPaymentMode())
                .paymentDate(payment.getPaymentDate())
                .transactionId(payment.getTransactionId())
                .remainingAmount(newPendingAmount)
                .build();
    }

    public List<PaymentResponse> getPaymentsByInvoice(Long invoiceId) {
        FeeInvoice invoice = feeInvoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

        return feePaymentRepository.findByInvoiceId(invoiceId)
                .stream()
                .map(p -> PaymentResponse.builder()
                        .id(p.getId())
                        .receiptNumber(p.getReceiptNumber())
                        .invoiceId(invoice.getId())
                        .invoiceNumber(invoice.getInvoiceNumber())
                        .studentName(invoice.getStudent().getFullName())
                        .amountPaid(p.getAmountPaid())
                        .paymentMode(p.getPaymentMode())
                        .paymentDate(p.getPaymentDate())
                        .transactionId(p.getTransactionId())
                        .remainingAmount(invoice.getPendingAmount())
                        .build())
                .toList();
    }

    // ─── Mappers ──────────────────────────────────────────────

    private FeeStructureResponse mapStructureToResponse(FeeStructure s) {
        return FeeStructureResponse.builder()
                .id(s.getId())
                .programId(s.getProgram().getId())
                .programName(s.getProgram().getName())
                .semesterId(s.getSemester().getId())
                .semesterNumber(s.getSemester().getSemesterNumber())
                .academicSession(s.getAcademicSession())
                .tuitionFee(s.getTuitionFee())
                .examFee(s.getExamFee())
                .developmentFee(s.getDevelopmentFee())
                .hostelFee(s.getHostelFee())
                .otherFee(s.getOtherFee())
                .totalAmount(s.getTotalAmount())
                .build();
    }

    private FeeInvoiceResponse mapInvoiceToResponse(FeeInvoice i) {
        return FeeInvoiceResponse.builder()
                .id(i.getId())
                .invoiceNumber(i.getInvoiceNumber())
                .studentId(i.getStudent().getId())
                .studentName(i.getStudent().getFullName())
                .feeStructureId(i.getFeeStructure().getId())
                .academicSession(i.getFeeStructure().getAcademicSession())
                .totalAmount(i.getTotalAmount())
                .paidAmount(i.getPaidAmount())
                .pendingAmount(i.getPendingAmount())
                .paymentStatus(i.getPaymentStatus())
                .invoiceDate(i.getInvoiceDate())
                .dueDate(i.getDueDate())
                .build();
    }
}