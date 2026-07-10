package com.kshitij.collegeerp.models.student.service;


import com.kshitij.collegeerp.academic.batch.entity.Batch;
import com.kshitij.collegeerp.academic.batch.repository.BatchRepository;
import com.kshitij.collegeerp.academic.department.entity.Department;
import com.kshitij.collegeerp.academic.department.repository.DepartmentRepository;
import com.kshitij.collegeerp.academic.program.entity.Program;
import com.kshitij.collegeerp.academic.program.repository.ProgramRepository;
import com.kshitij.collegeerp.academic.section.entity.Section;
import com.kshitij.collegeerp.academic.section.repository.SectionRepository;
import com.kshitij.collegeerp.academic.semester.entity.Semester;
import com.kshitij.collegeerp.academic.semester.repository.SemesterRepository;
import com.kshitij.collegeerp.auth.entity.Role;
import com.kshitij.collegeerp.auth.entity.User;
import com.kshitij.collegeerp.auth.repository.UserRepository;
import com.kshitij.collegeerp.common.exception.BadRequestException;
import com.kshitij.collegeerp.common.exception.ResourceNotFoundException;
import com.kshitij.collegeerp.models.student.dto.StudentRequest;
import com.kshitij.collegeerp.models.student.dto.StudentResponse;
import com.kshitij.collegeerp.models.student.entity.Student;
import com.kshitij.collegeerp.models.student.entity.StudentStatus;
import com.kshitij.collegeerp.models.student.repository.StudentRepository;
import com.kshitij.collegeerp.models.student.specification.StudentSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;
    private final ProgramRepository programRepository;
    private final BatchRepository batchRepository;
    private final SemesterRepository semesterRepository;
    private final SectionRepository sectionRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional
    public StudentResponse create(StudentRequest request) {
        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered: " + request.getEmail());
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        Program program = programRepository.findById(request.getProgramId())
                .orElseThrow(() -> new ResourceNotFoundException("Program not found"));

        if (!program.getDepartment().getId().equals(department.getId())) {
            throw new BadRequestException("Program does not belong to the selected department");
        }

        Batch batch = batchRepository.findById(request.getBatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));

        if (!batch.getProgram().getId().equals(program.getId())) {
            throw new RuntimeException("Batch does not belong to the selected department");
        }

        Semester semester = semesterRepository.findById(request.getSemesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Semester not found"));

        if (!semester.getBatch().getId().equals(batch.getId())) {
            throw new RuntimeException("Semester does not belong to the selected batch");
        }

        Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Section not found"));

        if (!section.getSemester().getId().equals(semester.getId())) {
            throw new RuntimeException("Section does not belong to the selected semester");
        }

        String enrollmentNumber = generateEnrollmentNumber(department.getCode());
        User user = createUser(request);

        Student student = Student.builder()
                .user(user)
                .enrollmentNumber(enrollmentNumber)
                .fullName(request.getFullName())
                .email(request.getEmail())
                .rollNumber(request.getRollNumber())
                .registrationNumber(request.getRegistrationNumber())
                .phone(request.getPhone())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .bloodGroup(request.getBloodGroup())
                .address(request.getAddress())
                .guardianName(request.getGuardianName())
                .guardianPhone(request.getGuardianPhone())
                .guardianRelation(request.getGuardianRelation())
                .department(department)
                .program(program)
                .batch(batch)
                .semester(semester)
                .section(section)
                .status(StudentStatus.ACTIVE)
                .admissionDate(request.getAdmissionDate() != null
                        ? request.getAdmissionDate() : LocalDate.now())
                .build();

        Student saved = studentRepository.save(student);
        return mapToResponse(saved);
    }

    private User createUser(StudentRequest request) {
        User user = User.builder()
               .email(request.getEmail())
                .password(passwordEncoder.encode("Student@123"))
                .fullName(request.getFullName())
                .role(Role.STUDENT)
                .profilePicture(null)
                .enabled(true)
                .build();

        return userRepository.save(user);
    }

    public Page<StudentResponse> getAllStudents(
            Pageable pageable,
            String keyword,
            Long departmentId,
            Long programId,
            Long batchId,
            Long semesterId,
            Long sectionId,
            StudentStatus status
    ) {

        return studentRepository.findAll(
                StudentSpecification.search(
                        keyword,
                        departmentId,
                        programId,
                        batchId,
                        semesterId,
                        sectionId,
                        status
                ),
                pageable
        ).map(this::mapToResponse);

    }

    public List<StudentResponse> getBySection(Long sectionId) {
        return studentRepository.findBySectionId(sectionId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public StudentResponse getById(Long id) {
        return mapToResponse(findStudentById(id));
    }

    @Transactional
    public StudentResponse update(Long id, StudentRequest request) {
        Student student = findStudentById(id);

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        Program program = programRepository.findById(request.getProgramId())
                .orElseThrow(() -> new ResourceNotFoundException("Program not found"));

        if (!program.getDepartment().getId().equals(department.getId())) {
            throw new RuntimeException("Program does not belong to the selected department");
        }

        Batch batch = batchRepository.findById(request.getBatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));

        if (!batch.getProgram().getId().equals(program.getId())) {
            throw new RuntimeException("Batch does not belong to the selected program");
        }

        Semester semester = semesterRepository.findById(request.getSemesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Semester not found"));

        if (!semester.getBatch().getId().equals(batch.getId())) {
            throw new RuntimeException("Semester does not belong to the selected batch");
        }

        Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Section not found"));

        if (!section.getSemester().getId().equals(semester.getId())) {
            throw new RuntimeException("Section does not belong to the selected semester");
        }

        student.setFullName(request.getFullName());
        student.setEmail(request.getEmail());
        student.setPhone(request.getPhone());
        student.setRollNumber(request.getRollNumber());
        student.setRegistrationNumber(request.getRegistrationNumber());
        student.setGender(request.getGender());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setBloodGroup(request.getBloodGroup());
        student.setAddress(request.getAddress());
        student.setGuardianName(request.getGuardianName());
        student.setGuardianPhone(request.getGuardianPhone());
        student.setGuardianRelation(request.getGuardianRelation());
        student.setDepartment(department);
        student.setProgram(program);
        student.setBatch(batch);
        student.setSemester(semester);
        student.setSection(section);

        return mapToResponse(studentRepository.save(student));
    }

    @Transactional
    public void updateStatus(Long id, StudentStatus status) {
        Student student = findStudentById(id);
        student.setStatus(status);
        studentRepository.save(student);
    }

    @Transactional
    public void delete(Long id) {
        studentRepository.delete(findStudentById(id));
    }

    private Student findStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student not found with id: " + id));
    }

    private String generateEnrollmentNumber(String departmentCode) {
        int year = Year.now().getValue();
        long count = studentRepository.count() + 1;
        return String.format("%d%s%04d", year, departmentCode, count);
        // Example: 2026CSE0001
    }

    private StudentResponse mapToResponse(Student student) {
        return StudentResponse.builder()
                .id(student.getId())
                .enrollmentNumber(student.getEnrollmentNumber())
                .rollNumber(student.getRollNumber())
                .registrationNumber(student.getRegistrationNumber())
                .fullName(student.getFullName())
                .email(student.getEmail())
                .phone(student.getPhone())
                .gender(student.getGender())
                .dateOfBirth(student.getDateOfBirth())
                .bloodGroup(student.getBloodGroup())
                .address(student.getAddress())
                .guardianName(student.getGuardianName())
                .guardianPhone(student.getGuardianPhone())
                .guardianRelation(student.getGuardianRelation())
                .photoUrl(student.getPhotoUrl())
                .departmentId(student.getDepartment().getId())
                .departmentName(student.getDepartment().getName())
                .programId(student.getProgram().getId())
                .programName(student.getProgram().getName())
                .batchId(student.getBatch().getId())
                .batchName(student.getBatch().getName())
                .semesterId(student.getSemester().getId())
                .semesterNumber(student.getSemester().getSemesterNumber())
                .sectionId(student.getSection().getId())
                .sectionName(student.getSection().getName())
                .status(student.getStatus())
                .admissionDate(student.getAdmissionDate())
                .build();
    }



}