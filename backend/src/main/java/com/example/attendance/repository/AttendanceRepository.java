package com.example.attendance.repository;

import com.example.attendance.model.Attendance;
import com.example.attendance.model.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByFacultyAndDate(Faculty faculty, LocalDate date);

    List<Attendance> findByFacultyAndDateBetween(Faculty faculty, LocalDate start, LocalDate end);

    List<Attendance> findByDate(LocalDate date);

    @Query("select a from Attendance a where a.faculty = :faculty and a.date between :start and :end")
    List<Attendance> findByFacultyAndMonth(@Param("faculty") Faculty faculty,
                                           @Param("start") LocalDate start,
                                           @Param("end") LocalDate end);
}

