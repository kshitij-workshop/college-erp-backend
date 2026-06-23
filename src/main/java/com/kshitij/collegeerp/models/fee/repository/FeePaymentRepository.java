package com.kshitij.collegeerp.models.fee.repository;

import com.kshitij.collegeerp.models.fee.entity.FeePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeePaymentRepository extends JpaRepository<FeePayment, Long> {
    List<FeePayment> findByInvoiceId(Long invoiceId);
}