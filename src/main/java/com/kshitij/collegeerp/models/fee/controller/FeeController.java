package com.kshitij.collegeerp.models.fee.controller;

import com.kshitij.collegeerp.common.response.ApiResponse;
import com.kshitij.collegeerp.models.fee.dto.*;
import com.kshitij.collegeerp.models.fee.service.FeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fees")
@RequiredArgsConstructor
public class FeeController {

    private final FeeService feeService;

    // ─── Fee Structure ────────────────────────────────────────

    @PostMapping("/structures")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<FeeStructureResponse>> createStructure(
            @Valid @RequestBody FeeStructureRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Fee structure created successfully",
                        feeService.createFeeStructure(request)));
    }

    @GetMapping("/structures")
    public ResponseEntity<ApiResponse<List<FeeStructureResponse>>> getAllStructures() {
        return ResponseEntity.ok(
                ApiResponse.success("Fee structures fetched",
                        feeService.getAllFeeStructures()));
    }

    @GetMapping("/structures/program/{programId}")
    public ResponseEntity<ApiResponse<List<FeeStructureResponse>>> getByProgram(
            @PathVariable Long programId) {
        return ResponseEntity.ok(
                ApiResponse.success("Fee structures fetched",
                        feeService.getFeeStructuresByProgram(programId)));
    }

    // ─── Invoice ──────────────────────────────────────────────

    @PostMapping("/invoices/generate")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<FeeInvoiceResponse>> generateInvoice(
            @RequestParam Long studentId,
            @RequestParam Long feeStructureId) {
        return ResponseEntity.ok(
                ApiResponse.success("Invoice generated successfully",
                        feeService.generateInvoice(studentId, feeStructureId)));
    }

    @GetMapping("/invoices/student/{studentId}")
    public ResponseEntity<ApiResponse<List<FeeInvoiceResponse>>> getInvoicesByStudent(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(
                ApiResponse.success("Invoices fetched",
                        feeService.getInvoicesByStudent(studentId)));
    }

    @GetMapping("/invoices/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<List<FeeInvoiceResponse>>> getPendingInvoices() {
        return ResponseEntity.ok(
                ApiResponse.success("Pending invoices fetched",
                        feeService.getPendingInvoices()));
    }

    // ─── Payment ──────────────────────────────────────────────

    @PostMapping("/payments")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<PaymentResponse>> recordPayment(
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Payment recorded successfully",
                        feeService.recordPayment(request)));
    }

    @GetMapping("/payments/invoice/{invoiceId}")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByInvoice(
            @PathVariable Long invoiceId) {
        return ResponseEntity.ok(
                ApiResponse.success("Payments fetched",
                        feeService.getPaymentsByInvoice(invoiceId)));
    }
}