package com.example.attendance.controller;

import com.example.attendance.model.Attendance;
import com.example.attendance.model.Faculty;
import com.example.attendance.model.LeaveRequest;
import com.example.attendance.repository.FacultyRepository;
import com.example.attendance.service.AttendanceService;
import com.example.attendance.service.LeaveService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/faculty")
public class FacultyController {

    private final FacultyRepository facultyRepository;
    private final AttendanceService attendanceService;
    private final LeaveService leaveService;

    public FacultyController(FacultyRepository facultyRepository,
            AttendanceService attendanceService,
            LeaveService leaveService) {
        this.facultyRepository = facultyRepository;
        this.attendanceService = attendanceService;
        this.leaveService = leaveService;
    }

    private Faculty getCurrentFaculty(Authentication authentication) {
        String username = authentication.getName();
        return facultyRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Faculty not found"));
    }

    @GetMapping("/attendance/daily")
    public List<Attendance> getDailyAttendance(Authentication authentication,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Faculty faculty = getCurrentFaculty(authentication);
        return attendanceService.getDailyAttendanceForFaculty(faculty, date);
    }

    @GetMapping("/attendance/monthly")
    public Map<String, Object> getMonthlyAttendance(Authentication authentication,
            @RequestParam("year") int year,
            @RequestParam("month") int month) {
        Faculty faculty = getCurrentFaculty(authentication);
        YearMonth ym = YearMonth.of(year, month);
        List<Attendance> records = attendanceService.getAttendanceForFacultyInMonth(faculty, ym);
        double percentage = attendanceService.calculateMonthlyPercentage(faculty, ym);
        Map<String, Object> resp = new HashMap<>();
        resp.put("records", records);
        resp.put("percentage", percentage);
        return resp;
    }

    @PostMapping("/leave")
    public LeaveRequest applyLeave(Authentication authentication,
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam("reason") String reason,
            @RequestParam(value = "startTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam(value = "endTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {
        Faculty faculty = getCurrentFaculty(authentication);
        return leaveService.applyLeave(faculty, from, to, reason, startTime, endTime);
    }

    @GetMapping("/leave")
    public List<LeaveRequest> myLeaves(Authentication authentication) {
        Faculty faculty = getCurrentFaculty(authentication);
        return leaveService.getLeavesForFaculty(faculty);
    }

    @PostMapping("/attendance/mark")
    public Attendance markNow(Authentication authentication) {
        Faculty faculty = getCurrentFaculty(authentication);
        return attendanceService.markAttendanceOnLogin(faculty);
    }

    @GetMapping("/attendance/today")
    public Map<String, Object> getTodayAttendance(Authentication authentication) {
        Faculty faculty = getCurrentFaculty(authentication);
        Attendance attendance = attendanceService.getTodayAttendance(faculty);
        Map<String, Object> resp = new HashMap<>();
        if (attendance != null) {
            resp.put("id", attendance.getId());
            resp.put("date", attendance.getDate());
            resp.put("loginTime", attendance.getLoginTime());
            resp.put("logoutTime", attendance.getLogoutTime());
            resp.put("status", attendance.getStatus());
        }
        return resp;
    }

    @GetMapping("/stats")
    public Map<String, Object> getStatistics(Authentication authentication) {
        Faculty faculty = getCurrentFaculty(authentication);
        int workingDays = attendanceService.getWorkingDaysCount(faculty);
        int presentDays = attendanceService.getPresentDaysCount(faculty);
        int absentDays = attendanceService.getAbsentDaysCount(faculty);
        double approvedLeaves = leaveService.getApprovedLeavesCount(faculty);

        Map<String, Object> stats = new HashMap<>();
        stats.put("facultyName", faculty.getName());
        stats.put("facultyDepartment", faculty.getDepartment());
        stats.put("facultyEmail", faculty.getUsername());
        stats.put("facultyRollNo", faculty.getRollNo());
        stats.put("workingDays", workingDays);
        stats.put("presentDays", presentDays);
        stats.put("absentDays", absentDays);
        stats.put("approvedLeaves", approvedLeaves);
        return stats;
    }

    @PostMapping("/attendance/verify-qr")
    public Attendance verifyQR(Authentication authentication,
            @RequestParam("token") String token,
            @RequestParam(value = "date", required = false) String qrDate,
            @RequestParam(value = "status", required = false) String status) {
        Faculty faculty = getCurrentFaculty(authentication);
        return attendanceService.verifyAndMarkAttendanceByQR(faculty, token, qrDate, status);
    }
}
