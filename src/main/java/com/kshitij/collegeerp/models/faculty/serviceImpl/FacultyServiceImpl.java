package com.kshitij.collegeerp.models.faculty.serviceImpl;

import com.kshitij.collegeerp.academic.department.entity.Department;
import com.kshitij.collegeerp.academic.department.repository.DepartmentRepository;
import com.kshitij.collegeerp.auth.entity.Role;
import com.kshitij.collegeerp.auth.entity.User;
import com.kshitij.collegeerp.auth.repository.RefreshTokenRepository;
import com.kshitij.collegeerp.auth.repository.UserRepository;
import com.kshitij.collegeerp.common.exception.ResourceNotFoundException;
import com.kshitij.collegeerp.common.response.PageResponse;
import com.kshitij.collegeerp.models.faculty.dto.FacultyRequest;
import com.kshitij.collegeerp.models.faculty.dto.FacultyResponse;
import com.kshitij.collegeerp.models.faculty.dto.FacultySummaryResponse;
import com.kshitij.collegeerp.models.faculty.entity.Designation;
import com.kshitij.collegeerp.models.faculty.entity.Faculty;
import com.kshitij.collegeerp.models.faculty.entity.FacultyStatus;
import com.kshitij.collegeerp.models.faculty.mapper.FacultyMapper;
import com.kshitij.collegeerp.models.faculty.repository.FacultyRepository;
import com.kshitij.collegeerp.models.faculty.service.FacultyService;
import com.kshitij.collegeerp.models.faculty.specification.FacultySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;

@Service
@RequiredArgsConstructor
@Transactional
public class FacultyServiceImpl implements FacultyService {

    private final FacultyRepository facultyRepository;

    private final DepartmentRepository departmentRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final FacultyMapper facultyMapper;

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public FacultyResponse createFaculty(FacultyRequest request) {

        validateEmail(request.getEmail());

        Department department = getDepartment(request.getDepartmentId());

        validateHod(
                department,
                request.getDesignation(),
                null
        );

        String employeeCode =
                generateEmployeeCode(department.getCode());

        User user = createUser(request);

        Faculty faculty = Faculty.builder()

                .user(user)

                .employeeCode(employeeCode)

                .fullName(request.getFullName())

                .email(request.getEmail())

                .phone(request.getPhone())

                .gender(request.getGender())

                .dateOfBirth(request.getDateOfBirth())

                .bloodGroup(request.getBloodGroup())

                .address(request.getAddress())

                .designation(request.getDesignation())

                .qualification(request.getQualification())

                .specialization(request.getSpecialization())

                .experienceYears(request.getExperienceYears())

                .joiningDate(
                        request.getJoiningDate() == null
                                ? LocalDate.now()
                                : request.getJoiningDate()
                )

                .photoUrl(request.getPhotoUrl())

                .department(department)

                .status(FacultyStatus.ACTIVE)

                .build();

        return facultyMapper.toResponse(

                facultyRepository.save(faculty)

        );

    }

    @Override
    public FacultyResponse updateFaculty(
            Long facultyId,
            FacultyRequest request
    ) {

        Faculty faculty = getFaculty(facultyId);

        if (!faculty.getEmail().equalsIgnoreCase(request.getEmail())
                && facultyRepository.existsByEmail(request.getEmail())) {

            throw new IllegalArgumentException(
                    "Faculty with this email already exists."
            );
        }

        Department department = getDepartment(
                request.getDepartmentId()
        );

        validateHod(
                department,
                request.getDesignation(),
                facultyId
        );

        User user = faculty.getUser();

        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());

        faculty.setFullName(request.getFullName());
        faculty.setEmail(request.getEmail());
        faculty.setPhone(request.getPhone());
        faculty.setGender(request.getGender());
        faculty.setDateOfBirth(request.getDateOfBirth());
        faculty.setBloodGroup(request.getBloodGroup());
        faculty.setAddress(request.getAddress());

        faculty.setDesignation(request.getDesignation());
        faculty.setQualification(request.getQualification());
        faculty.setSpecialization(request.getSpecialization());
        faculty.setExperienceYears(request.getExperienceYears());
        faculty.setJoiningDate(request.getJoiningDate());
        faculty.setPhotoUrl(request.getPhotoUrl());

        faculty.setDepartment(department);

        userRepository.save(user);

        return facultyMapper.toResponse(

                facultyRepository.save(faculty)

        );

    }

    @Override
    @Transactional(readOnly = true)
    public FacultyResponse getFacultyById(Long facultyId) {

        Faculty faculty = getFaculty(facultyId);

        return facultyMapper.toResponse(faculty);

    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FacultySummaryResponse> getAllFaculty(

            String search,

            Long departmentId,

            Designation designation,

            FacultyStatus status,

            Pageable pageable

    ) {

        Page<Faculty> facultyPage = facultyRepository.findAll(

                FacultySpecification.filter(

                        search,

                        departmentId,

                        designation,

                        status

                ),

                pageable

        );

        Page<FacultySummaryResponse> responsePage = facultyPage.map(

                facultyMapper::toSummaryResponse

        );

        return PageResponse.from(responsePage);

    }


    @Override
    public void deleteFaculty(Long facultyId) {

        Faculty faculty = getFaculty(facultyId);

        User user = faculty.getUser();

        facultyRepository.delete(faculty);

        if (user != null) {

            refreshTokenRepository.deleteByUser(user);

            userRepository.delete(user);

        }

    }


    private void validateEmail(String email) {

        if (facultyRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Faculty with email '" + email + "' already exists."
            );
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "User with email '" + email + "' already exists."
            );
        }

    }

    private Department getDepartment(Long departmentId) {

        return departmentRepository.findById(departmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department not found with id : " + departmentId
                        )
                );

    }

    private Faculty getFaculty(Long facultyId) {

        return facultyRepository.findById(facultyId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Faculty not found with id : " + facultyId
                        )
                );

    }

    private void validateHod(

            Department department,

            Designation designation,

            Long facultyId

    ) {

        if (designation != Designation.HOD) {
            return;
        }

        boolean hodExists;

        if (facultyId == null) {

            hodExists = facultyRepository
                    .existsByDepartmentAndDesignationAndStatus(

                            department,

                            Designation.HOD,

                            FacultyStatus.ACTIVE

                    );

        } else {

            hodExists = facultyRepository
                    .existsByDepartmentAndDesignationAndStatusAndIdNot(

                            department,

                            Designation.HOD,

                            FacultyStatus.ACTIVE,

                            facultyId

                    );

        }

        if (hodExists) {

            throw new IllegalArgumentException(

                    "This department already has an active HOD."

            );

        }

    }

    private String generateEmployeeCode(String departmentCode) {

        int year = Year.now().getValue();

        long count = facultyRepository.count() + 1;

        return String.format(

                "EMP-%d%s%03d",

                year,

                departmentCode.toUpperCase(),

                count

        );

    }

    private User createUser(
            FacultyRequest request) {

        User user = User.builder()

                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode("Faculty@123"))
                .role(Role.FACULTY)
                .enabled(true)
                .profilePicture(null)
                .build();

        return userRepository.save(user);

    }
}

