package com.example.attendance.service;

import com.example.attendance.model.Attendance;
import com.example.attendance.model.Faculty;
import com.example.attendance.model.LeaveRequest;
import com.example.attendance.repository.AttendanceRepository;
import com.example.attendance.repository.LeaveRequestRepository;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    // Define morning and evening windows
    private static final LocalTime MORNING_START = LocalTime.of(0, 0);
    private static final LocalTime MORNING_END = LocalTime.of(23, 59);
    private static final LocalTime EVENING_START = LocalTime.of(0, 0);
    private static final LocalTime EVENING_END = LocalTime.of(23, 59);

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
     * - Morning window: create today's attendance with loginTime (PRESENT) unless
     * on approved leave
     * - Evening window: if attendance exists without logoutTime, set logoutTime;
     * else create a record with logoutTime
     */
    public Attendance markAttendanceOnLogin(Faculty faculty) {
        LocalDate today = LocalDate.now();

        Optional<Attendance> existing = attendanceRepository.findByFacultyAndDate(faculty, today);

        if (existing.isPresent()) {
            Attendance a = existing.get();
            // If loginTime is null, consider it a check-in
            if (a.getLoginTime() == null) {
                a.setLoginTime(LocalDateTime.now());
                a.setStatus("PRESENT");
            }
            // If it has loginTime but no logoutTime, consider it a check-out
            else if (a.getLogoutTime() == null) {
                a.setLogoutTime(LocalDateTime.now());
                // Make sure status is PRESENT
                a.setStatus("PRESENT");
            }
            return attendanceRepository.save(a);
        }

        // Doesn't exist, create for check-in
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

    /**
     * Scheduled helper to mark absent for users who didn't check-in by morning end.
     */
    public void markAbsenteesForToday() {
        LocalDate today = LocalDate.now();
        List<Attendance> todays = attendanceRepository.findByDate(today);
        // assume faculty list via attendance records only insufficient; leave as TODO:
        // better fetch all faculty
        // For simplicity, we will not enumerate all faculty here in this helper
        // in-memory run.
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
        if (totalWorkingDays == 0)
            return 0.0;
        return (presentDays * 100.0) / totalWorkingDays;
    }

    public List<Attendance> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByDate(date);
    }

    public Attendance getTodayAttendance(Faculty faculty) {
        LocalDate today = LocalDate.now();
        return attendanceRepository.findByFacultyAndDate(faculty, today).orElse(null);
    }

    public Attendance markAttendanceByAdmin(Faculty faculty, LocalDate date, String status) {
        if (date == null) {
            throw new IllegalArgumentException("Date is required");
        }
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Status is required");
        }
        String normalizedStatus = status.trim().toUpperCase();
        if (!"PRESENT".equals(normalizedStatus) && !"ABSENT".equals(normalizedStatus)
                && !"LEAVE".equals(normalizedStatus) && !"HALF_FN".equals(normalizedStatus)
                && !"HALF_AN".equals(normalizedStatus)) {
            throw new IllegalArgumentException("Status must be PRESENT, ABSENT, LEAVE, HALF_FN or HALF_AN");
        }

        Attendance attendance = attendanceRepository.findByFacultyAndDate(faculty, date)
                .orElseGet(Attendance::new);
        attendance.setFaculty(faculty);
        attendance.setDate(date);
        attendance.setStatus(normalizedStatus);

        if ("PRESENT".equals(normalizedStatus)) {
            if (attendance.getLoginTime() == null)
                attendance.setLoginTime(date.atTime(9, 0));
            if (attendance.getLogoutTime() == null)
                attendance.setLogoutTime(date.atTime(17, 0));
        } else if ("HALF_FN".equals(normalizedStatus)) {
            attendance.setLoginTime(date.atTime(9, 0));
            attendance.setLogoutTime(date.atTime(12, 0));
        } else if ("HALF_AN".equals(normalizedStatus)) {
            attendance.setLoginTime(date.atTime(12, 30));
            attendance.setLogoutTime(date.atTime(16, 0));
        } else {
            attendance.setLoginTime(null);
            attendance.setLogoutTime(null);
        }

        return attendanceRepository.save(attendance);
    }

    public int getWorkingDaysCount(Faculty faculty) {
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        LocalDate end = LocalDate.now();
        List<Attendance> records = attendanceRepository.findByFacultyAndDateBetween(faculty, start, end);
        return records.size();
    }

    public int getPresentDaysCount(Faculty faculty) {
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        LocalDate end = LocalDate.now();
        List<Attendance> records = attendanceRepository.findByFacultyAndDateBetween(faculty, start, end);
        return (int) records.stream()
                .filter(a -> "PRESENT".equalsIgnoreCase(a.getStatus()))
                .count();
    }

    public int getAbsentDaysCount(Faculty faculty) {
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        LocalDate end = LocalDate.now();
        List<Attendance> records = attendanceRepository.findByFacultyAndDateBetween(faculty, start, end);
        return (int) records.stream()
                .filter(a -> "ABSENT".equalsIgnoreCase(a.getStatus()))
                .count();
    }

    public int getTodayPresentCount() {
        LocalDate today = LocalDate.now();
        List<Attendance> todayRecords = attendanceRepository.findByDate(today);
        return (int) todayRecords.stream()
                .filter(a -> "PRESENT".equalsIgnoreCase(a.getStatus()))
                .count();
    }

    public int getTodayAbsentCount() {
        LocalDate today = LocalDate.now();
        List<Attendance> todayRecords = attendanceRepository.findByDate(today);
        return (int) todayRecords.stream()
                .filter(a -> "ABSENT".equalsIgnoreCase(a.getStatus()))
                .count();
    }

    public Attendance verifyAndMarkAttendanceByQR(Faculty faculty, String token, String qrDate, String status) {
        if (token == null || !token.startsWith("ATT_SESSIONS_")) {
            throw new IllegalArgumentException("Invalid QR Token Format");
        }

        try {
            String[] parts = token.split("_");
            if (parts.length < 4)
                throw new IllegalArgumentException("Invalid QR Structure");

            // Extract timestamp from the end (e.g., _Date.now())
            long tokenTimestamp = Long.parseLong(parts[parts.length - 1]);
            long currentMillis = System.currentTimeMillis();

            // Enforce 20-second validity (with a bit of margin for server time drift and
            // scan latency)
            if (currentMillis - tokenTimestamp > 25000) { // 25s margin
                throw new IllegalStateException("QR Code has expired. Please scan a new one.");
            }

            // If qrDate is provided, ensure it matches today
            if (qrDate != null && !qrDate.isEmpty()) {
                LocalDate qDate = LocalDate.parse(qrDate);
                if (!qDate.equals(LocalDate.now())) {
                    throw new IllegalArgumentException("QR Code is for a different date: " + qrDate);
                }
            }

            LocalDate dateObj = (qrDate != null && !qrDate.isEmpty()) ? LocalDate.parse(qrDate) : LocalDate.now();
            String finalStatus = (status != null && !status.isEmpty()) ? status : "PRESENT";
            return markAttendanceByAdmin(faculty, dateObj, finalStatus);

        } catch (Exception e) {
            if (e instanceof IllegalStateException || e instanceof IllegalArgumentException) {
                throw e;
            }
            throw new IllegalArgumentException("Failed to decode QR: " + e.getMessage());
        }
    }
}
