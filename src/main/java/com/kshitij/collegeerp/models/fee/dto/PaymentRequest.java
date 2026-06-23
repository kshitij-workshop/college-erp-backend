package com.kshitij.collegeerp.models.fee.dto;

import com.kshitij.collegeerp.models.fee.entity.PaymentMode;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PaymentRequest {

    @NotNull(message = "Invoice ID is required")
    private Long invoiceId;

    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be greater than 0")
    private Double amountPaid;

    @NotNull(message = "Payment mode is required")
    private PaymentMode paymentMode;

    private String transactionId;
}