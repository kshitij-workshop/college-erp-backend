package com.kshitij.collegeerp.models.library.controller;

import com.kshitij.collegeerp.common.response.ApiResponse;
import com.kshitij.collegeerp.models.library.dto.*;
import com.kshitij.collegeerp.models.library.service.LibraryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
public class LibraryController {

    private final LibraryService libraryService;

    // ─── Book endpoints ───────────────────────────────────────

    @PostMapping("/books")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<BookResponse>> addBook(
            @Valid @RequestBody BookRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Book added successfully",
                        libraryService.addBook(request)));
    }

    @GetMapping("/books")
    public ResponseEntity<ApiResponse<List<BookResponse>>> getAllBooks() {
        return ResponseEntity.ok(
                ApiResponse.success("Books fetched successfully",
                        libraryService.getAllBooks()));
    }

    @GetMapping("/books/available")
    public ResponseEntity<ApiResponse<List<BookResponse>>> getAvailableBooks() {
        return ResponseEntity.ok(
                ApiResponse.success("Available books fetched",
                        libraryService.getAvailableBooks()));
    }

    @GetMapping("/books/search")
    public ResponseEntity<ApiResponse<List<BookResponse>>> searchByTitle(
            @RequestParam String title) {
        return ResponseEntity.ok(
                ApiResponse.success("Search results",
                        libraryService.searchByTitle(title)));
    }

    @GetMapping("/books/category")
    public ResponseEntity<ApiResponse<List<BookResponse>>> getByCategory(
            @RequestParam String category) {
        return ResponseEntity.ok(
                ApiResponse.success("Books by category",
                        libraryService.getByCategory(category)));
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> getBookById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Book fetched",
                        libraryService.getBookById(id)));
    }

    @PutMapping("/books/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Book updated successfully",
                        libraryService.updateBook(id, request)));
    }

    // ─── Issue / Return endpoints ─────────────────────────────

    @PostMapping("/issue")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<BookIssueResponse>> issueBook(
            @Valid @RequestBody BookIssueRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Book issued successfully",
                        libraryService.issueBook(request)));
    }

    @PatchMapping("/return/{issueId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<BookIssueResponse>> returnBook(
            @PathVariable Long issueId) {
        return ResponseEntity.ok(
                ApiResponse.success("Book returned successfully",
                        libraryService.returnBook(issueId)));
    }

    @GetMapping("/issues/student/{studentId}")
    public ResponseEntity<ApiResponse<List<BookIssueResponse>>> getIssuesByStudent(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(
                ApiResponse.success("Issues fetched",
                        libraryService.getIssuesByStudent(studentId)));
    }

    @GetMapping("/issues/student/{studentId}/active")
    public ResponseEntity<ApiResponse<List<BookIssueResponse>>> getActiveIssuesByStudent(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(
                ApiResponse.success("Active issues fetched",
                        libraryService.getActiveIssuesByStudent(studentId)));
    }

    @GetMapping("/issues/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<List<BookIssueResponse>>> getAllActiveIssues() {
        return ResponseEntity.ok(
                ApiResponse.success("All active issues fetched",
                        libraryService.getAllActiveIssues()));
    }

    @GetMapping("/issues/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<List<BookIssueResponse>>> getOverdueBooks() {
        return ResponseEntity.ok(
                ApiResponse.success("Overdue books fetched",
                        libraryService.getOverdueBooks()));
    }
}