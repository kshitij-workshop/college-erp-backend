package com.kshitij.collegeerp.models.subject.service;

import com.kshitij.collegeerp.academic.program.entity.Program;
import com.kshitij.collegeerp.academic.program.repository.ProgramRepository;
import com.kshitij.collegeerp.common.exception.ResourceNotFoundException;
import com.kshitij.collegeerp.models.subject.dto.SubjectRequest;
import com.kshitij.collegeerp.models.subject.dto.SubjectResponse;
import com.kshitij.collegeerp.models.subject.entity.Subject;
import com.kshitij.collegeerp.models.subject.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final ProgramRepository programRepository;

    @Transactional
    public SubjectResponse create(SubjectRequest request) {
        if (subjectRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Subject code already exists: " + request.getCode());
        }

        Program program = programRepository.findById(request.getProgramId())
                .orElseThrow(() -> new ResourceNotFoundException("Program not found"));

        if (request.getSemesterNumber() > program.getTotalSemesters()) {
            throw new RuntimeException("Semester number exceeds program's total semesters ("
                    + program.getTotalSemesters() + ")");
        }

        Subject subject = Subject.builder()
                .name(request.getName())
                .code(request.getCode().toUpperCase())
                .credits(request.getCredits())
                .type(request.getType())
                .program(program)
                .semesterNumber(request.getSemesterNumber())
                .active(true)
                .build();

        Subject saved = subjectRepository.save(subject);
        return mapToResponse(saved);
    }

    public List<SubjectResponse> getAll() {
        return subjectRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<SubjectResponse> getByProgramAndSemester(Long programId, Integer semesterNumber) {
        return subjectRepository.findByProgramIdAndSemesterNumber(programId, semesterNumber)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public SubjectResponse getById(Long id) {
        return mapToResponse(findSubjectById(id));
    }

    @Transactional
    public SubjectResponse update(Long id, SubjectRequest request) {
        Subject subject = findSubjectById(id);

        Program program = programRepository.findById(request.getProgramId())
                .orElseThrow(() -> new ResourceNotFoundException("Program not found"));

        if (request.getSemesterNumber() > program.getTotalSemesters()) {
            throw new RuntimeException("Semester number exceeds program's total semesters ("
                    + program.getTotalSemesters() + ")");
        }

        subject.setName(request.getName());
        subject.setCode(request.getCode().toUpperCase());
        subject.setCredits(request.getCredits());
        subject.setType(request.getType());
        subject.setProgram(program);
        subject.setSemesterNumber(request.getSemesterNumber());

        return mapToResponse(subjectRepository.save(subject));
    }

    @Transactional
    public void deactivate(Long id) {
        Subject subject = findSubjectById(id);
        subject.setActive(false);
        subjectRepository.save(subject);
    }

    @Transactional
    public void delete(Long id) {
        subjectRepository.delete(findSubjectById(id));
    }

    private Subject findSubjectById(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subject not found with id: " + id));
    }

    private SubjectResponse mapToResponse(Subject subject) {
        return SubjectResponse.builder()
                .id(subject.getId())
                .name(subject.getName())
                .code(subject.getCode())
                .credits(subject.getCredits())
                .type(subject.getType())
                .programId(subject.getProgram().getId())
                .programName(subject.getProgram().getName())
                .semesterNumber(subject.getSemesterNumber())
                .active(subject.isActive())
                .build();
    }
}