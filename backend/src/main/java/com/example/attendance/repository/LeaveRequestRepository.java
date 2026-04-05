package com.example.attendance.repository;

import com.example.attendance.model.Faculty;
import com.example.attendance.model.LeaveRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    @EntityGraph(attributePaths = "faculty")
    List<LeaveRequest> findByFaculty(Faculty faculty);

    @EntityGraph(attributePaths = "faculty")
    List<LeaveRequest> findByStatus(String status);

    @EntityGraph(attributePaths = "faculty")
    Optional<LeaveRequest> findDetailedById(Long id);

    @EntityGraph(attributePaths = "faculty")
    List<LeaveRequest> findAllByOrderByIdDesc();

    List<LeaveRequest> findByFacultyAndFromDateLessThanEqualAndToDateGreaterThanEqual(
            Faculty faculty,
            LocalDate date1,
            LocalDate date2
    );
}

