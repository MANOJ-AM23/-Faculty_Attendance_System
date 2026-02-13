package com.example.attendance.service;

import com.example.attendance.model.Attendance;
import com.example.attendance.model.Faculty;
import com.example.attendance.model.LeaveRequest;
import com.example.attendance.repository.AttendanceRepository;
import com.example.attendance.repository.LeaveRequestRepository;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    // Define morning and evening windows
    private static final LocalTime MORNING_START = LocalTime.of(9, 0);
    private static final LocalTime MORNING_END = LocalTime.of(11, 0);
    private static final LocalTime EVENING_START = LocalTime.of(17, 0);
    private static final LocalTime EVENING_END = LocalTime.of(19, 0);

    public AttendanceService(AttendanceRepository attendanceRepository,
                             LeaveRequestRepository leaveRequestRepository) {
        this.attendanceRepository = attendanceRepository;
        this.leaveRequestRepository = leaveRequestRepository;
    }

    /**
     * Mark attendance for faculty on login.
     * - Only within working hours
     * - Only once per day
     * - If on approved leave, mark LEAVE instead
     */
    /**
     * Mark attendance based on current time:
     * - Morning window: create today's attendance with loginTime (PRESENT) unless on approved leave
     * - Evening window: if attendance exists without logoutTime, set logoutTime; else create a record with logoutTime
     */
    public Attendance markAttendanceOnLogin(Faculty faculty) {
        LocalDate today = LocalDate.now();
        LocalTime nowTime = LocalTime.now();

        Optional<Attendance> existing = attendanceRepository.findByFacultyAndDate(faculty, today);

        // Morning check-in
        if (!nowTime.isBefore(MORNING_START) && !nowTime.isAfter(MORNING_END)) {
            if (existing.isPresent()) {
                Attendance a = existing.get();
                if (a.getStatus() == null) a.setStatus("PRESENT");
                if (a.getLoginTime() == null) a.setLoginTime(LocalDateTime.now());
                return attendanceRepository.save(a);
            }

            Attendance attendance = new Attendance();
            attendance.setFaculty(faculty);
            attendance.setDate(today);
            attendance.setLoginTime(LocalDateTime.now());

            boolean onApprovedLeave = isOnApprovedLeave(faculty, today);
            if (onApprovedLeave) {
                attendance.setStatus("LEAVE");
            } else {
                attendance.setStatus("PRESENT");
            }
            return attendanceRepository.save(attendance);
        }

        // Evening check-out
        if (!nowTime.isBefore(EVENING_START) && !nowTime.isAfter(EVENING_END)) {
            if (existing.isPresent()) {
                Attendance a = existing.get();
                if (a.getLogoutTime() == null) {
                    a.setLogoutTime(LocalDateTime.now());
                    return attendanceRepository.save(a);
                }
                return a;
            } else {
                // create a record with logoutTime only
                Attendance attendance = new Attendance();
                attendance.setFaculty(faculty);
                attendance.setDate(today);
                attendance.setLogoutTime(LocalDateTime.now());
                attendance.setStatus("PRESENT");
                return attendanceRepository.save(attendance);
            }
        }

        throw new IllegalStateException("Attendance action is allowed only during morning or evening windows");
    }

    /**
     * Scheduled helper to mark absent for users who didn't check-in by morning end.
     */
    public void markAbsenteesForToday() {
        LocalDate today = LocalDate.now();
        List<Attendance> todays = attendanceRepository.findByDate(today);
        // assume faculty list via attendance records only insufficient; leave as TODO: better fetch all faculty
        // For simplicity, we will not enumerate all faculty here in this helper in-memory run.
    }

    private boolean isOnApprovedLeave(Faculty faculty, LocalDate date) {
        List<LeaveRequest> leaves = leaveRequestRepository
                .findByFacultyAndFromDateLessThanEqualAndToDateGreaterThanEqual(faculty, date, date);
        return leaves.stream().anyMatch(l -> "APPROVED".equalsIgnoreCase(l.getStatus()));
    }

    public List<Attendance> getDailyAttendanceForFaculty(Faculty faculty, LocalDate date) {
        return attendanceRepository.findByFacultyAndDateBetween(faculty, date, date);
    }

    public List<Attendance> getAttendanceForFacultyInMonth(Faculty faculty, YearMonth month) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        return attendanceRepository.findByFacultyAndMonth(faculty, start, end);
    }

    public double calculateMonthlyPercentage(Faculty faculty, YearMonth month) {
        List<Attendance> list = getAttendanceForFacultyInMonth(faculty, month);
        long presentDays = list.stream()
                .filter(a -> "PRESENT".equalsIgnoreCase(a.getStatus()))
                .count();

        int totalWorkingDays = month.lengthOfMonth(); // simple assumption: all days
        if (totalWorkingDays == 0) return 0.0;
        return (presentDays * 100.0) / totalWorkingDays;
    }

    public List<Attendance> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByDate(date);
    }
}

