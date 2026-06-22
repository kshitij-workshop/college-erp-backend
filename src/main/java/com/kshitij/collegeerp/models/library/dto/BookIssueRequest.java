package com.kshitij.collegeerp.models.library.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BookIssueRequest {

    @NotNull(message = "Book ID is required")
    private Long bookId;

    @NotNull(message = "Student ID is required")
    private Long studentId;
}