package com.kshitij.collegeerp.academic.batch.service;

import com.kshitij.collegeerp.academic.batch.dto.BatchRequest;
import com.kshitij.collegeerp.academic.batch.dto.BatchResponse;
import com.kshitij.collegeerp.academic.batch.entity.Batch;
import com.kshitij.collegeerp.academic.batch.repository.BatchRepository;
import com.kshitij.collegeerp.academic.department.entity.Department;
import com.kshitij.collegeerp.academic.program.entity.Program;
import com.kshitij.collegeerp.academic.program.repository.ProgramRepository;
import com.kshitij.collegeerp.common.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final BatchRepository batchRepository;
    private final ProgramRepository programRepository;

    @Transactional
    public BatchResponse create(BatchRequest request) {
        if(batchRepository.existsByNameAndProgramId(request.getName(), request.getProgramId())){
            throw new RuntimeException("Batch already exists for this program: " + request.getName());
        }

        Program program = programRepository.findById(request.getProgramId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Program not found with id: " + request.getProgramId()
                ));

        Batch batch = Batch.builder()
                .name(request.getName())
                .startYear(request.getStartYear())
                .endYear(request.getEndYear())
                .program(program)
                .active(true)
                .build();

        Batch saved = batchRepository.save(batch);

        return mapToResponse(saved);

    }

    public List<BatchResponse> getAll() {
        return batchRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<BatchResponse> getByProgram(Long programId) {
        return batchRepository.findByProgramId(programId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public BatchResponse getById(Long id) {
        Batch batch = findBatchById(id);
        return mapToResponse(batch);
    }

    @Transactional
    public BatchResponse update(Long id, BatchRequest request) {
        Batch batch = findBatchById(id);

        Program program = programRepository.findById(request.getProgramId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Program not found with id " + request.getProgramId()
                ));

        batch.setName(request.getName());
        batch.setStartYear(request.getStartYear());
        batch.setEndYear(request.getEndYear());
        batch.setProgram(program);

        Batch updated = batchRepository.save(batch);

        return mapToResponse(updated);
    }

    @Transactional
    public void deactivate(Long id) {
        Batch batch = findBatchById(id);
        batch.setActive(false);
        batchRepository.save(batch);
    }

    @Transactional
    public void delete(Long id) {
        Batch batch = findBatchById(id);
        batchRepository.delete(batch);
    }

    private Batch findBatchById(Long id) {
        return batchRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException(
                        "Batch not found with id " + id
                ));
    }


    private BatchResponse mapToResponse(Batch batch) {

        Program program = batch.getProgram();

        Department department = program.getDepartment();

        return BatchResponse.builder()
                .id(batch.getId())
                .name(batch.getName())
                .startYear(batch.getStartYear())
                .endYear(batch.getEndYear())

                .programId(program.getId())
                .programName(program.getName())

                .departmentId(department.getId())
                .departmentName(department.getName())

                .active(batch.isActive())
                .build();
    }



}
