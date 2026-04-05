## Faculty Attendance Management System (Spring Boot + MySQL)

### 1. Technology Stack
- **Frontend**: HTML, CSS, JavaScript (static pages)
- **Backend**: Java 17, Spring Boot (Web, Security, Data JPA)
- **Database**: MySQL
- **Architecture**: Three-tier (Controller → Service → Repository/DB)

### 2. Database Schema (MySQL)
Run these SQL commands once (or let JPA create similar tables automatically via `ddl-auto=update`):

```sql
CREATE DATABASE faculty_attendance;
USE faculty_attendance;

CREATE TABLE faculty (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);

CREATE TABLE attendance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    faculty_id BIGINT NOT NULL,
    date DATE NOT NULL,
    login_time DATETIME NULL,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT fk_att_fac FOREIGN KEY (faculty_id) REFERENCES faculty(id)
);

CREATE TABLE leave_request (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    faculty_id BIGINT NOT NULL,
    from_date DATE NOT NULL,
    to_date DATE NOT NULL,
    reason VARCHAR(500) NOT NULL,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT fk_leave_fac FOREIGN KEY (faculty_id) REFERENCES faculty(id)
);
```

### 3. Configure Database Connection
Edit `src/main/resources/application.properties` and set:
- **spring.datasource.username** to your MySQL user (e.g. `root`)
- **spring.datasource.password** to your MySQL password

### 4. Build & Run
1. Install **Java 17+** and **Maven**.
2. In a terminal, go to the project folder:
   - `cd c:\Users\91636\Desktop\Attendence`
3. Build:
   - `mvn clean package`
4. Run:
   - `mvn spring-boot:run`
### 5. Default Usage Flow
- Insert an admin row directly in DB (example):

```sql
INSERT INTO faculty (name, department, username, password, role)
VALUES ('Admin User', 'Management', 'admin',
        '$2a$10$3CiYQUAE9qN5Sb8gJtIyriD9Ul4yqZFrsm5P4znMo8Vw5/BRBxbxG', -- password = admin123
        'ADMIN');
```

- Login as admin (`admin` / `admin123`) to add faculty.
- Faculty logs in:
  - Successful login within working hours auto-marks **PRESENT** (or **LEAVE** if approved).
  - Faculty can view daily/monthly attendance and apply for leave.
- Admin can:
  - Add/update/delete faculty
  - View attendance by date/month
  - Approve/reject leave requests

### 6. Attendance Logic
- **Login within time window (09:00–11:00)** → `PRESENT`
- **Approved leave on date** → `LEAVE`
- **No login & no leave** → implicitly `ABSENT` (no row: treat as absent in reports)
- Duplicate attendance is prevented by checking an existing record for the day.

