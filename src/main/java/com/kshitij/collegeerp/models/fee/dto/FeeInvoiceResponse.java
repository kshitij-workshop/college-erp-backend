package com.kshitij.collegeerp.models.fee.dto;

import com.kshitij.collegeerp.models.fee.entity.PaymentStatus;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class FeeInvoiceResponse {
    private Long id;
    private String invoiceNumber;
    private Long studentId;
    private String studentName;
    private String enrollmentNumber;
    private Long feeStructureId;
    private String academicSession;
    private Double totalAmount;
    private Double paidAmount;
    private Double pendingAmount;
    private PaymentStatus paymentStatus;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
}