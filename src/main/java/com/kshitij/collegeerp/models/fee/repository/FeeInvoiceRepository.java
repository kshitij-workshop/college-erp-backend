package com.kshitij.collegeerp.models.fee.repository;

import com.kshitij.collegeerp.models.fee.entity.FeeInvoice;
import com.kshitij.collegeerp.models.fee.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FeeInvoiceRepository extends JpaRepository<FeeInvoice, Long> {
    List<FeeInvoice> findByStudentId(Long studentId);
    List<FeeInvoice> findByPaymentStatus(PaymentStatus status);
    List<FeeInvoice> findByPaymentStatusIn(List<PaymentStatus> statuses);
    Optional<FeeInvoice> findByInvoiceNumber(String invoiceNumber);
    boolean existsByStudentIdAndFeeStructureId(Long studentId, Long feeStructureId);
}