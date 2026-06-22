package com.kshitij.collegeerp.models.library.dto;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String publisher;
    private Integer publishedYear;
    private String category;
    private Integer totalCopies;
    private Integer availableCopies;
    private boolean active;
}