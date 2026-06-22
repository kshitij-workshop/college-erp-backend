package com.kshitij.collegeerp.models.library.repository;

import com.kshitij.collegeerp.models.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);
    List<Book> findByCategoryIgnoreCase(String category);
    List<Book> findByTitleContainingIgnoreCase(String title);
    List<Book> findByAvailableCopiesGreaterThan(Integer copies);
}
