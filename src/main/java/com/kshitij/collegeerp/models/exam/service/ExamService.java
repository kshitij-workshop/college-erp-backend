package com.kshitij.collegeerp.models.exam.service;

import com.kshitij.collegeerp.common.exception.ResourceNotFoundException;
import com.kshitij.collegeerp.models.exam.dto.ExamRequest;
import com.kshitij.collegeerp.models.exam.dto.ExamResponse;
import com.kshitij.collegeerp.models.exam.entity.Exam;
import com.kshitij.collegeerp.models.exam.repository.ExamRepository;
import com.kshitij.collegeerp.models.subject.entity.SubjectOffering;
import com.kshitij.collegeerp.models.subject.repository.SubjectOfferingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final SubjectOfferingRepository subjectOfferingRepository;

    @Transactional
    public ExamResponse create(ExamRequest request) {
        SubjectOffering offering = subjectOfferingRepository.findById(request.getSubjectOfferingId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject offering not found"));

        Exam exam = Exam.builder()
                .name(request.getName())
                .examType(request.getExamType())
                .maxMarks(request.getMaxMarks())
                .subjectOffering(offering)
                .examDate(request.getExamDate())
                .resultPublished(false)
                .locked(false)
                .build();

        Exam saved = examRepository.save(exam);

        return mapToResponse(saved);
    }

    public List<ExamResponse> getAll() {
        return examRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<ExamResponse> getBySubjectOffering(Long subjectOfferingId) {
        return examRepository.findBySubjectOfferingId(subjectOfferingId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ExamResponse getById(Long id) {
        return mapToResponse(findExamById(id));
    }

    @Transactional
    public void publishResult(Long id) {
        Exam exam = findExamById(id);
        if(!exam.isLocked()) {
            throw new RuntimeException("Can not publish result befor locking marks");
        }

        exam.setResultPublished(true);
        examRepository.save(exam);
    }

    @Transactional
    public void lockExam(Long id) {
        Exam exam = findExamById(id);
        exam.setLocked(true);
        examRepository.save(exam);
    }

    @Transactional
    public void delete(Long id) {
        Exam exam = findExamById(id);
        if(exam.isLocked()) {
            throw new RuntimeException("Cannot delete a locked exam");
        }
    }

    public Exam findExamById(Long id) {
        return examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id " + id));
    }

    private ExamResponse mapToResponse(Exam exam) {
        return ExamResponse.builder()
                .id(exam.getId())
                .name(exam.getName())
                .examType(exam.getExamType())
                .maxMarks(exam.getMaxMarks())
                .subjectOfferingId(exam.getSubjectOffering().getId())
                .subjectName(exam.getSubjectOffering().getSubject().getName())
                .sectionName(exam.getSubjectOffering().getSection().getName())
                .examDate(exam.getExamDate())
                .resultPublished(exam.isResultPublished())
                .locked(exam.isLocked())
                .build();
    }
}
