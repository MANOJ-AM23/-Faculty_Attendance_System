package com.example.attendance.controller;

import com.example.attendance.model.Attendance;
import com.example.attendance.model.Faculty;
import com.example.attendance.model.LeaveRequest;
import com.example.attendance.service.AttendanceService;
import com.example.attendance.service.FacultyService;
import com.example.attendance.service.LeaveService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final FacultyService facultyService;
    private final AttendanceService attendanceService;
    private final LeaveService leaveService;

    public AdminController(FacultyService facultyService,
                           AttendanceService attendanceService,
                           LeaveService leaveService) {
        this.facultyService = facultyService;
        this.attendanceService = attendanceService;
        this.leaveService = leaveService;
    }

    // Faculty CRUD
    @GetMapping("/faculty")
    public List<Faculty> getAllFaculty() {
        return facultyService.getAllFaculty();
    }

    @PostMapping("/faculty")
    public Faculty createFaculty(@RequestBody Faculty faculty) {
        return facultyService.createFaculty(faculty);
    }

    @PutMapping("/faculty/{id}")
    public Faculty updateFaculty(@PathVariable Long id, @RequestBody Faculty updated) {
        Faculty existing = facultyService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Faculty not found"));
        existing.setName(updated.getName());
        existing.setDepartment(updated.getDepartment());
        existing.setUsername(updated.getUsername());
        if (updated.getPassword() != null && !updated.getPassword().isBlank()) {
            existing.setPassword(updated.getPassword());
        }
        existing.setRole(updated.getRole());
        return facultyService.createFaculty(existing);
    }

    @DeleteMapping("/faculty/{id}")
    public void deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
    }

    // Attendance views
    @GetMapping("/attendance/date")
    public List<Attendance> attendanceByDate(@RequestParam("date")
                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                             LocalDate date) {
        return attendanceService.getAttendanceByDate(date);
    }

    @GetMapping("/attendance/faculty/{facultyId}/month")
    public List<Attendance> attendanceByFacultyAndMonth(@PathVariable Long facultyId,
                                                        @RequestParam("year") int year,
                                                        @RequestParam("month") int month) {
        Faculty faculty = facultyService.getById(facultyId)
                .orElseThrow(() -> new IllegalArgumentException("Faculty not found"));
        return attendanceService.getAttendanceForFacultyInMonth(faculty, YearMonth.of(year, month));
    }

    // Leave approvals
    @GetMapping("/leave/pending")
    public List<LeaveRequest> pendingLeaves() {
        return leaveService.getPendingLeaves();
    }

    @GetMapping("/leave")
    public List<LeaveRequest> allLeaves() {
        return leaveService.getAllLeaves();
    }

    @PostMapping("/leave/{id}/approve")
    public LeaveRequest approve(@PathVariable Long id) {
        return leaveService.approveLeave(id);
    }

    @PostMapping("/leave/{id}/reject")
    public LeaveRequest reject(@PathVariable Long id,
                               @RequestParam("reason") String reason) {
        return leaveService.rejectLeave(id, reason);
    }

    @PostMapping("/attendance/mark")
    public Attendance markAttendance(@RequestParam("facultyId") Long facultyId,
                                     @RequestParam("date")
                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                     LocalDate date,
                                     @RequestParam("status") String status) {
        Faculty faculty = facultyService.getById(facultyId)
                .orElseThrow(() -> new IllegalArgumentException("Faculty not found"));
        return attendanceService.markAttendanceByAdmin(faculty, date, status);
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        int todayPresent = attendanceService.getTodayPresentCount();
        int todayAbsent = attendanceService.getTodayAbsentCount();
        
        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("todayPresent", todayPresent);
        stats.put("todayAbsent", todayAbsent);
        return stats;
    }
}

