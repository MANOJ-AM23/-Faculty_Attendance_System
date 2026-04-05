package com.example.attendance.service;

import com.example.attendance.model.Faculty;
import com.example.attendance.repository.FacultyRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final PasswordEncoder passwordEncoder;

    public FacultyService(FacultyRepository facultyRepository, PasswordEncoder passwordEncoder) {
        this.facultyRepository = facultyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Faculty createFaculty(Faculty faculty) {
        if (faculty.getPassword() == null || faculty.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (!faculty.getPassword().startsWith("$2a$")) {
            faculty.setPassword(passwordEncoder.encode(faculty.getPassword()));
        }
        if (faculty.getRole() == null || faculty.getRole().isBlank()) {
            faculty.setRole("FACULTY");
        } else if ("END_USER".equalsIgnoreCase(faculty.getRole())) {
            faculty.setRole("FACULTY");
        }
        return facultyRepository.save(faculty);
    }

    public List<Faculty> getAllFaculty() {
        return facultyRepository.findAll();
    }

    public Optional<Faculty> getById(Long id) {
        return facultyRepository.findById(id);
    }

    public void deleteFaculty(Long id) {
        facultyRepository.deleteById(id);
    }

    public Optional<Faculty> findByUsername(String username) {
        return facultyRepository.findByUsername(username);
    }
}

