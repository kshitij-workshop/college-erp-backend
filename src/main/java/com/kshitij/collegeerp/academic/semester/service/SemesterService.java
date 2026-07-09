package com.kshitij.collegeerp.academic.semester.service;

import com.kshitij.collegeerp.academic.batch.entity.Batch;
import com.kshitij.collegeerp.academic.batch.repository.BatchRepository;
import com.kshitij.collegeerp.academic.department.entity.Department;
import com.kshitij.collegeerp.academic.program.entity.Program;
import com.kshitij.collegeerp.academic.semester.dto.SemesterRequest;
import com.kshitij.collegeerp.academic.semester.dto.SemesterResponse;
import com.kshitij.collegeerp.academic.semester.entity.Semester;
import com.kshitij.collegeerp.academic.semester.repository.SemesterRepository;
import com.kshitij.collegeerp.common.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SemesterService {

    private final SemesterRepository semesterRepository;
    private final BatchRepository batchRepository;

    @Transactional
    public SemesterResponse create(SemesterRequest request) {

        if (semesterRepository.existsBySemesterNumberAndBatchId(
                request.getSemesterNumber(),
                request.getBatchId())) {

            throw new IllegalArgumentException(
                    "Semester " + request.getSemesterNumber()
                            + " already exists for this batch."
            );
        }

        Batch batch = batchRepository.findById(request.getBatchId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Batch not found with id " + request.getBatchId()
                ));

        // Ensure only one current semester per batch
        if (request.isCurrent()) {

            semesterRepository.findByBatchIdAndCurrentTrue(request.getBatchId())
                    .ifPresent(existing -> {
                        existing.setCurrent(false);
                        semesterRepository.save(existing);
                    });

        }

        Semester semester = Semester.builder()
                .semesterNumber(request.getSemesterNumber())
                .batch(batch)
                .active(true)
                .current(request.isCurrent())
                .build();

        Semester saved = semesterRepository.save(semester);

        return mapToResponse(saved);
    }

    public List<SemesterResponse> getAll() {
        return semesterRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<SemesterResponse> getByBatch(Long batchId) {
        return semesterRepository.findByBatchId(batchId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public SemesterResponse getById(Long id) {
        Semester semester = findSemesterById(id);
        return mapToResponse(semester);
    }

    @Transactional
    public SemesterResponse update(Long id, SemesterRequest request) {

        Semester semester = findSemesterById(id);

        if ((!semester.getSemesterNumber().equals(request.getSemesterNumber())
                || !semester.getBatch().getId().equals(request.getBatchId()))
                && semesterRepository.existsBySemesterNumberAndBatchId(
                request.getSemesterNumber(),
                request.getBatchId())) {

            throw new IllegalArgumentException(
                    "Semester " + request.getSemesterNumber()
                            + " already exists for this batch."
            );
        }

        Batch batch = batchRepository.findById(request.getBatchId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Batch not found with id " + request.getBatchId()
                ));

        // Ensure only one current semester per batch
        if (request.isCurrent()) {

            semesterRepository.findByBatchIdAndCurrentTrue(request.getBatchId())
                    .ifPresent(existing -> {

                        if (!existing.getId().equals(semester.getId())) {

                            existing.setCurrent(false);
                            semesterRepository.save(existing);

                        }

                    });

        }

        semester.setSemesterNumber(request.getSemesterNumber());
        semester.setBatch(batch);
        semester.setCurrent(request.isCurrent());

        Semester updated = semesterRepository.save(semester);

        return mapToResponse(updated);
    }
    @Transactional
    public void deactivate(Long id) {
        Semester semester = findSemesterById(id);
        semester.setActive(false);
        semesterRepository.save(semester);
    }

    @Transactional
    public void delete(Long id) {
        Semester semester = findSemesterById(id);
        semesterRepository.delete(semester);
    }

    private Semester findSemesterById(Long id) {
        return semesterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Semester not found with id " + id));
    }


    private SemesterResponse mapToResponse(Semester semester) {

        Batch batch = semester.getBatch();

        Program program = batch.getProgram();

        Department department = program.getDepartment();

        return SemesterResponse.builder()

                .id(semester.getId())

                .semesterNumber(semester.getSemesterNumber())

                .departmentId(department.getId())
                .departmentName(department.getName())

                .programId(program.getId())
                .programName(program.getName())

                .batchId(batch.getId())
                .batchName(batch.getName())

                .active(semester.isActive())

                .current(semester.isCurrent())

                .build();
    }
}
