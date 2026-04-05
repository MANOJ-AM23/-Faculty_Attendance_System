# Database Storage Guide for Presentations

To show your "mam" how the database works, you can present these three different aspects of data management.

## 1. Java Code (The 'Clean' Way)
Show this snippet to explain how Spring Boot saves data using JPA. This is the logic used by the `FacultyRepository`.

```java
// How we save a new Faculty member in Java
@Autowired
private FacultyRepository facultyRepository;

public void saveDummyFaculty() {
    // 1. Create a new Faculty object
    Faculty faculty = new Faculty();
    faculty.setName("Dr. Jane Doe");
    faculty.setDepartment("Computer Science");
    faculty.setUsername("jane_doe");
    faculty.setRole("FACULTY");
    faculty.setRollNo("FAC101");

    // 2. Hash the password (for security)
    String encryptedPassword = passwordEncoder.encode("secret123");
    faculty.setPassword(encryptedPassword);

    // 3. Save to database in a single line!
    facultyRepository.save(faculty); 
    
    System.out.println("Faculty saved successfully!");
}
```

---

## 2. Live Database Viewer (H2 Console)
I have enabled a **Live Database Console** for your project. You can show this directly to your teacher to see all tables and data while the app is running!

1. Open your browser and go to: **[http://localhost:8080/h2-console](http://localhost:8080/h2-console)**
2. Set the **JDBC URL** to: `jdbc:h2:mem:testdb`
3. Set the **User Name** to: `sa`
4. Leave **Password** empty.
5. Click **Connect**. 

You will be able to see all 190+ faculty members and their attendance records!

---

## 3. SQL Script (Raw Dummy Data)
If you are using MySQL or want to show a script in class, use this to insert 10 dummy faculty records.

```sql
-- Initializing 10 dummy faculty members
INSERT INTO faculty (name, department, username, password, role, roll_no) VALUES 
('Aarav Sharma', 'Mathematics', 'aarav_s', '$2a$10$3Ci...', 'FACULTY', 'FAC001'),
('Vivaan Verma', 'Science', 'vivaan_v', '$2a$10$3Ci...', 'FACULTY', 'FAC002'),
('Aditya Gupta', 'English', 'aditya_g', '$2a$10$3Ci...', 'FACULTY', 'FAC003'),
('Vihaan Malhotra', 'Social Studies', 'vihaan_m', '$2a$10$3Ci...', 'FACULTY', 'FAC004'),
('Arjun Singh', 'Hindi', 'arjun_s', '$2a$10$3Ci...', 'FACULTY', 'FAC005'),
('Sai Patel', 'Physical Education', 'sai_p', '$2a$10$3Ci...', 'FACULTY', 'FAC006'),
('Ayaan Reddy', 'Computer Science', 'ayaan_r', '$2a$10$3Ci...', 'FACULTY', 'FAC007'),
('Krishna Rao', 'Arts', 'krishna_r', '$2a$10$3Ci...', 'FACULTY', 'FAC008'),
('Ishaan Nair', 'Biology', 'ishaan_n', '$2a$10$3Ci...', 'FACULTY', 'FAC009'),
('Shaurya Iyer', 'Chemistry', 'shaurya_i', '$2a$10$3Ci...', 'FACULTY', 'FAC010');
```

---

## 4. Key Talking Points
During your presentation, emphasize these three points:
1. **Persistence:** Explain that data is saved permanently in the database (or in-memory for testing).
2. **Entity Mapping:** Explain that the `Faculty` class in Java represents the `faculty` table in the database.
3. **Relationships:** Explain that the `Attendance` table is linked to the `Faculty` table using a **Foreign Key** (`faculty_id`).
