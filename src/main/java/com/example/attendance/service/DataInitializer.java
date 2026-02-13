package com.example.attendance.service;

import com.example.attendance.model.Faculty;
import com.example.attendance.repository.FacultyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (facultyRepository.findByUsername("admin").isEmpty()) {
            Faculty admin = new Faculty();
            admin.setName("Administrator");
            admin.setDepartment("Administration");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole("ADMIN");
            facultyRepository.save(admin);
        }
        if (facultyRepository.findByUsername("faculty1").isEmpty()) {
            Faculty f = new Faculty();
            f.setName("First Faculty");
            f.setDepartment("Computer Science");
            f.setUsername("faculty1");
            f.setPassword(passwordEncoder.encode("faculty1"));
            f.setRole("FACULTY");
            facultyRepository.save(f);
        }
    }
}
