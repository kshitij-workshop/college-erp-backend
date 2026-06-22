package com.kshitij.collegeerp.models.library.service;

import com.kshitij.collegeerp.common.exception.ResourceNotFoundException;
import com.kshitij.collegeerp.models.library.dto.*;
import com.kshitij.collegeerp.models.library.entity.*;
import com.kshitij.collegeerp.models.library.repository.*;
import com.kshitij.collegeerp.models.student.entity.Student;
import com.kshitij.collegeerp.models.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LibraryService {

    private final BookRepository bookRepository;
    private final BookIssueRepository bookIssueRepository;
    private final StudentRepository studentRepository;

    private static final int LOAN_DAYS = 14;          // issued for 14 days
    private static final double FINE_PER_DAY = 2.0;   // ₹2 per day fine

    // ─── Book CRUD ───────────────────────────────────────────────

    @Transactional
    public BookResponse addBook(BookRequest request) {
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new RuntimeException("Book with ISBN already exists: " + request.getIsbn());
        }

        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .publisher(request.getPublisher())
                .publishedYear(request.getPublishedYear())
                .category(request.getCategory())
                .totalCopies(request.getTotalCopies())
                .availableCopies(request.getTotalCopies())
                .active(true)
                .build();

        return mapBookToResponse(bookRepository.save(book));
    }

    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::mapBookToResponse)
                .toList();
    }

    public List<BookResponse> searchByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(this::mapBookToResponse)
                .toList();
    }

    public List<BookResponse> getByCategory(String category) {
        return bookRepository.findByCategoryIgnoreCase(category)
                .stream()
                .map(this::mapBookToResponse)
                .toList();
    }

    public List<BookResponse> getAvailableBooks() {
        return bookRepository.findByAvailableCopiesGreaterThan(0)
                .stream()
                .map(this::mapBookToResponse)
                .toList();
    }

    public BookResponse getBookById(Long id) {
        return mapBookToResponse(findBookById(id));
    }

    @Transactional
    public BookResponse updateBook(Long id, BookRequest request) {
        Book book = findBookById(id);
        int diff = request.getTotalCopies() - book.getTotalCopies();

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublisher(request.getPublisher());
        book.setPublishedYear(request.getPublishedYear());
        book.setCategory(request.getCategory());
        book.setTotalCopies(request.getTotalCopies());
        book.setAvailableCopies(book.getAvailableCopies() + diff);

        return mapBookToResponse(bookRepository.save(book));
    }

    // ─── Issue / Return ──────────────────────────────────────────

    @Transactional
    public BookIssueResponse issueBook(BookIssueRequest request) {
        Book book = findBookById(request.getBookId());
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        // Validation: is book available ?
        if (book.getAvailableCopies() <= 0) {
            throw new RuntimeException("No copies available for: " + book.getTitle());
        }

        // Validation: already issued the same book to the student
        if (bookIssueRepository.existsByStudentIdAndBookIdAndStatus(
                student.getId(), book.getId(), IssueStatus.ISSUED)) {
            throw new RuntimeException(
                    "Student already has this book issued");
        }

        // Decreases available book copies
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        BookIssue issue = BookIssue.builder()
                .book(book)
                .student(student)
                .issueDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(LOAN_DAYS))
                .status(IssueStatus.ISSUED)
                .fineAmount(0.0)
                .build();

        return mapIssueToResponse(bookIssueRepository.save(issue));
    }

    @Transactional
    public BookIssueResponse returnBook(Long issueId) {
        BookIssue issue = bookIssueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Issue record not found with id: " + issueId));

        if (issue.getStatus() == IssueStatus.RETURNED) {
            throw new RuntimeException("Book already returned");
        }

        LocalDate today = LocalDate.now();
        issue.setReturnDate(today);
        issue.setStatus(IssueStatus.RETURNED);

        // Calculate late fine if late returning
        if (today.isAfter(issue.getDueDate())) {
            long overdueDays = ChronoUnit.DAYS.between(issue.getDueDate(), today);
            double fine = overdueDays * FINE_PER_DAY;
            issue.setFineAmount(fine);
        }

        // Increase Available copies
        Book book = issue.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return mapIssueToResponse(bookIssueRepository.save(issue));
    }

    // ─── Queries ─────────────────────────────────────────────────

    public List<BookIssueResponse> getIssuesByStudent(Long studentId) {
        return bookIssueRepository.findByStudentId(studentId)
                .stream()
                .map(this::mapIssueToResponse)
                .toList();
    }

    public List<BookIssueResponse> getActiveIssuesByStudent(Long studentId) {
        return bookIssueRepository.findByStudentIdAndStatus(studentId, IssueStatus.ISSUED)
                .stream()
                .map(this::mapIssueToResponse)
                .toList();
    }

    public List<BookIssueResponse> getAllActiveIssues() {
        return bookIssueRepository.findByStatus(IssueStatus.ISSUED)
                .stream()
                .map(this::mapIssueToResponse)
                .toList();
    }

    public List<BookIssueResponse> getOverdueBooks() {
        return bookIssueRepository.findByStatusAndDueDateBefore(
                        IssueStatus.ISSUED, LocalDate.now())
                .stream()
                .map(this::mapIssueToResponse)
                .toList();
    }

    // ─── Private helpers ─────────────────────────────────────────

    private Book findBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Book not found with id: " + id));
    }

    private BookResponse mapBookToResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .publisher(book.getPublisher())
                .publishedYear(book.getPublishedYear())
                .category(book.getCategory())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .active(book.isActive())
                .build();
    }

    private BookIssueResponse mapIssueToResponse(BookIssue issue) {
        return BookIssueResponse.builder()
                .id(issue.getId())
                .bookId(issue.getBook().getId())
                .bookTitle(issue.getBook().getTitle())
                .bookIsbn(issue.getBook().getIsbn())
                .studentId(issue.getStudent().getId())
                .studentName(issue.getStudent().getFullName())
                .enrollmentNumber(issue.getStudent().getEnrollmentNumber())
                .issueDate(issue.getIssueDate())
                .dueDate(issue.getDueDate())
                .returnDate(issue.getReturnDate())
                .status(issue.getStatus())
                .fineAmount(issue.getFineAmount())
                .build();
    }
}