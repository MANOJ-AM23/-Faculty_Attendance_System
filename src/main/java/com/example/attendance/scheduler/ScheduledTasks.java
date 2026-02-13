package com.example.attendance.scheduler;

import com.example.attendance.model.Attendance;
import com.example.attendance.model.Faculty;
import com.example.attendance.repository.AttendanceRepository;
import com.example.attendance.repository.FacultyRepository;
import com.example.attendance.repository.LeaveRequestRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ScheduledTasks {

    private final FacultyRepository facultyRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    public ScheduledTasks(FacultyRepository facultyRepository,
                          AttendanceRepository attendanceRepository,
                          LeaveRequestRepository leaveRequestRepository) {
        this.facultyRepository = facultyRepository;
        this.attendanceRepository = attendanceRepository;
        this.leaveRequestRepository = leaveRequestRepository;
    }

    // 11:05 AM daily: mark absentees who didn't check-in
    @Scheduled(cron = "0 5 11 * * *")
    public void markAbsentees() {
        LocalDate today = LocalDate.now();
        List<Faculty> all = facultyRepository.findAll();
        for (Faculty f : all) {
            boolean has = attendanceRepository.findByFacultyAndDate(f, today).isPresent();
            if (!has) {
                // check leave
                boolean onLeave = leaveRequestRepository
                        .findByFacultyAndFromDateLessThanEqualAndToDateGreaterThanEqual(f, today, today)
                        .stream().anyMatch(l -> "APPROVED".equalsIgnoreCase(l.getStatus()));
                Attendance a = new Attendance();
                a.setFaculty(f);
                a.setDate(today);
                a.setStatus(onLeave ? "LEAVE" : "ABSENT");
                attendanceRepository.save(a);
            }
        }
    }

    // 7:05 PM daily: auto set logoutTime for ones who didn't logout
    @Scheduled(cron = "0 5 19 * * *")
    public void autoLogout() {
        LocalDate today = LocalDate.now();
        List<Attendance> list = attendanceRepository.findByDate(today);
        for (Attendance a : list) {
            if (a.getLogoutTime() == null && "PRESENT".equalsIgnoreCase(a.getStatus())) {
                a.setLogoutTime(LocalDateTime.now());
                attendanceRepository.save(a);
            }
        }
    }
}
