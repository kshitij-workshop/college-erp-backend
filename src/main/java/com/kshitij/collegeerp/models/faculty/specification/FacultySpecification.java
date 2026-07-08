package com.kshitij.collegeerp.models.faculty.specification;

import com.kshitij.collegeerp.models.faculty.entity.Designation;
import com.kshitij.collegeerp.models.faculty.entity.Faculty;
import com.kshitij.collegeerp.models.faculty.entity.FacultyStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class FacultySpecification {

    private FacultySpecification() {
    }

    public static Specification<Faculty> filter(

            String search,

            Long departmentId,

            Designation designation,

            FacultyStatus status

    ) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // Search

            if (search != null && !search.isBlank()) {

                String keyword = "%" + search.trim().toLowerCase() + "%";

                predicates.add(

                        cb.or(

                                cb.like(cb.lower(root.get("fullName")), keyword),

                                cb.like(cb.lower(root.get("email")), keyword),

                                cb.like(cb.lower(root.get("employeeCode")), keyword),

                                cb.like(cb.lower(root.get("phone")), keyword)

                        )

                );

            }

            // Department

            if (departmentId != null) {

                predicates.add(

                        cb.equal(

                                root.get("department").get("id"),

                                departmentId

                        )

                );

            }

            // Designation

            if (designation != null) {

                predicates.add(

                        cb.equal(

                                root.get("designation"),

                                designation

                        )

                );

            }

            // Status

            if (status != null) {

                predicates.add(

                        cb.equal(

                                root.get("status"),

                                status

                        )

                );

            }

            return cb.and(predicates.toArray(new Predicate[0]));

        };

    }

}