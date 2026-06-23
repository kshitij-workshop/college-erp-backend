package com.kshitij.collegeerp.models.fee.dto;

import com.kshitij.collegeerp.models.fee.entity.PaymentMode;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private String receiptNumber;
    private Long invoiceId;
    private String invoiceNumber;
    private String studentName;
    private Double amountPaid;
    private PaymentMode paymentMode;
    private LocalDateTime paymentDate;
    private String transactionId;
    private Double remainingAmount;
}