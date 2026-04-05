package com.example.attendance.controller;

import com.example.attendance.model.Attendance;
import com.example.attendance.model.Faculty;
import com.example.attendance.repository.FacultyRepository;
import com.example.attendance.service.AttendanceService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Controller
public class AuthController {

    private final FacultyRepository facultyRepository;
    private final AttendanceService attendanceService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(FacultyRepository facultyRepository, AttendanceService attendanceService,
            PasswordEncoder passwordEncoder) {
        this.facultyRepository = facultyRepository;
        this.attendanceService = attendanceService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register_faculty")
    public String registerFaculty(@RequestParam String username, @RequestParam String password) {
        if (facultyRepository.findByUsername(username).isPresent()) {
            return "redirect:/login.html?error"; // simplistic error
        }
        Faculty saved = new Faculty();
        saved.setUsername(username);
        saved.setPassword(passwordEncoder.encode(password));
        saved.setRole("FACULTY");
        saved.setName(username); // simplified
        saved.setDepartment("General");
        facultyRepository.save(saved);
        return "redirect:/login.html?registered";
    }

    /**
     * Root path handler - redirect to login
     */
    @GetMapping("/")
    public String root() {
        return "redirect:/login.html";
    }

    /**
     * Called after successful login (configured as defaultSuccessUrl).
     * - For faculty: mark attendance (if within window and not duplicate) then
     * redirect to faculty dashboard
     * - For admin: redirect to admin dashboard
     */
    @GetMapping("/post-login")
    public String postLogin(Authentication authentication) {
        String username = authentication.getName();
        Optional<Faculty> facultyOpt = facultyRepository.findByUsername(username);
        if (facultyOpt.isEmpty()) {
            return "redirect:/login.html?error";
        }
        Faculty faculty = facultyOpt.get();

        boolean isFaculty = hasRole(authentication, "ROLE_FACULTY");
        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");

        if (isFaculty) {
            try {
                Attendance att = attendanceService.markAttendanceOnLogin(faculty);
            } catch (Exception ex) {
                // ignore error, but could log
            }
            return "redirect:/faculty-dashboard.html";
        } else if (isAdmin) {
            return "redirect:/admin-dashboard.html";
        } else {
            return "redirect:/login.html?error";
        }
    }

    private boolean hasRole(Authentication auth, String role) {
        for (GrantedAuthority authority : auth.getAuthorities()) {
            if (role.equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
