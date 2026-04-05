package com.example.attendance.service;

import com.example.attendance.model.Attendance;
import com.example.attendance.model.Faculty;
import com.example.attendance.model.LeaveRequest;
import com.example.attendance.repository.AttendanceRepository;
import com.example.attendance.repository.FacultyRepository;
import com.example.attendance.repository.LeaveRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Create admin user
        if (facultyRepository.findByUsername("admin").isEmpty()) {
            Faculty admin = new Faculty();
            admin.setName("School Admin");
            admin.setDepartment("Administration");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            facultyRepository.save(admin);
        }
        // Create demo faculty
        if (facultyRepository.findByUsername("mr_sharma").isEmpty()) {
            Faculty demoFac = new Faculty();
            demoFac.setName("Mr. Sharma");
            demoFac.setDepartment("Mathematics");
            demoFac.setUsername("mr_sharma");
            demoFac.setPassword(passwordEncoder.encode("password123"));
            demoFac.setRole("FACULTY");
            demoFac.setRollNo("FACSHARMA");
            facultyRepository.save(demoFac);
        }

        // Initialize 120 faculty
        String[] depts = { "Mathematics", "Science", "English", "Social Studies", "Hindi", "Physical Education",
                "Computer Science", "Arts", "Biology", "Chemistry", "Physics", "Geography", "History" };
        Random random = new Random(123);

        String[] firstNames = { "Aarav", "Vivaan", "Aditya", "Vihaan", "Arjun", "Sai", "Ayaan", "Krishna", "Ishaan",
                "Shaurya", "Ananya", "Diya", "Aditi", "Isha", "Riya", "Aarohi", "Anushka", "Nandini", "Priya",
                "Sneha", "Kabir", "Rohan", "Rahul", "Aman", "Raj", "Neha", "Pooja", "Vikram", "Suresh", "Ramesh",
                "Sunita", "Anita", "Geeta", "Sanjay", "Amit", "Alok", "Rajiv", "Meena", "Rekha", "Kiran", "Nisha",
                "Manoj", "Anil", "Deepak", "Prakash", "Gaurav", "Manish", "Rakesh", "Vikas", "Ashok", "Ravi",
                "Sandeep", "Karan", "Abhishek", "Tarun", "Yash", "Kartik", "Harsh", "Pranav", "Mohit", "Arun",
                "Jatin" };
        String[] lastNames = { "Sharma", "Verma", "Gupta", "Malhotra", "Singh", "Patel", "Reddy", "Rao", "Nair",
                "Iyer", "Kumar", "Chopra", "Joshi", "Tiwari", "Yadav", "Chauhan", "Bhatia", "Desai", "Jain",
                "Mehta", "Aggarwal", "Pandey", "Saxena", "Srivastava", "Kapoor" };

        System.out.println("Ensuring 190 faculty records and generating attendance...");

        for (int i = 1; i <= 190; i++) {
            String username = "faculty_" + i;
            if (facultyRepository.findByUsername(username).isEmpty()) {
                Faculty f = new Faculty();
                String firstName = firstNames[random.nextInt(firstNames.length)];
                String lastName = lastNames[random.nextInt(lastNames.length)];
                f.setName(firstName + " " + lastName);
                f.setRollNo("FAC" + String.format("%03d", i));
                f.setDepartment(depts[random.nextInt(depts.length)]);
                f.setUsername(username);
                f.setPassword(passwordEncoder.encode("password" + i));
                f.setRole("FACULTY");
                facultyRepository.save(f);
            }
        }

        List<Faculty> faculties = facultyRepository.findAll().stream()
                .filter(f -> "FACULTY".equalsIgnoreCase(f.getRole()))
                .toList();

        LocalDate today = LocalDate.now();

        for (Faculty f : faculties) {
            LocalDate facultyStart;
            try {
                String uname = f.getUsername();
                if (uname.startsWith("faculty_")) {
                    int index = Integer.parseInt(uname.substring(8));
                    if (index > 50) {
                        facultyStart = LocalDate.of(2026, 2, 15);
                    } else {
                        facultyStart = LocalDate.of(2026, 1, 1);
                    }
                } else {
                    facultyStart = LocalDate.of(2026, 1, 1);
                }
            } catch (Exception e) {
                facultyStart = LocalDate.of(2026, 1, 1);
            }

            // Generate attendance only if missing for this faculty in their range
            long existingCount = attendanceRepository.findByFacultyAndDateBetween(f, facultyStart, today).size();

            if (existingCount < 2) {
                System.out.println("Generating attendance for " + f.getUsername() + " from " + facultyStart);
                LocalDate curr = facultyStart;
                while (!curr.isAfter(today)) {
                    if (curr.getDayOfWeek().getValue() <= 5) { // Mon-Fri
                        Attendance att = new Attendance();
                        att.setFaculty(f);
                        att.setDate(curr);

                        int randValue = random.nextInt(100);
                        if (randValue < 85) { // 85% Present
                            att.setStatus("PRESENT");
                            att.setLoginTime(
                                    LocalDateTime.of(curr, LocalTime.of(8, 0).plusMinutes(50 + random.nextInt(15))));
                            att.setLogoutTime(LocalDateTime.of(curr, LocalTime.of(16, random.nextInt(30))));
                        } else if (randValue < 93) { // 8% Late
                            att.setStatus("LATE");
                            att.setLoginTime(LocalDateTime.of(curr, LocalTime.of(10, random.nextInt(60))));
                            att.setLogoutTime(LocalDateTime.of(curr, LocalTime.of(16, random.nextInt(30))));
                        } else if (randValue < 97) { // 4% Absent
                            att.setStatus("ABSENT");
                        } else { // 3% Leave
                            att.setStatus("LEAVE");
                            if (random.nextBoolean()) {
                                LeaveRequest lr = new LeaveRequest();
                                lr.setFaculty(f);
                                lr.setFromDate(curr);
                                lr.setToDate(curr);
                                lr.setStatus("APPROVED");
                                lr.setReason("Personal/Health");
                                leaveRequestRepository.save(lr);
                            }
                        }
                        attendanceRepository.save(att);
                    }
                    curr = curr.plusDays(1);
                }
            }
        }
        System.out.println("Data Initialization & Update Completed!");
    }
}
