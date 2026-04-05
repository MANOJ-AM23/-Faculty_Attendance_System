package com.example.attendance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import com.example.attendance.model.Faculty;
import com.example.attendance.repository.FacultyRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class FacultyAttendanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FacultyAttendanceApplication.class, args);
    }

    @Bean
    public CommandLineRunner seedAdmin(FacultyRepository repository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (repository.findByUsername("admin").isEmpty()) {
                Faculty admin = new Faculty();
                admin.setName("Admin User");
                admin.setDepartment("Management");
                admin.setUsername("admin");
                admin.setRollNo("ADM001");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole("ADMIN");
                repository.save(admin);
                System.out.println("Admin user seeded: admin / admin123");
            }
        };
    }
}
