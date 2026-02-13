package com.example.attendance.repository;

import com.example.attendance.model.Faculty;
import com.example.attendance.model.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByFaculty(Faculty faculty);

    List<LeaveRequest> findByStatus(String status);

    List<LeaveRequest> findByFacultyAndFromDateLessThanEqualAndToDateGreaterThanEqual(
            Faculty faculty,
            LocalDate date1,
            LocalDate date2
    );
}

