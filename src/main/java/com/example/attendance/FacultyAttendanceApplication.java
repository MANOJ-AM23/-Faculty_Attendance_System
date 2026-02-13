package com.example.attendance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FacultyAttendanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FacultyAttendanceApplication.class, args);
    }
}

