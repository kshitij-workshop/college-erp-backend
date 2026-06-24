# College ERP System — Backend

A production-style College ERP System backend built with Spring Boot, covering student lifecycle management, academic structure, attendance, exams, fees, library, timetable, assignments, and more.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 4.x |
| Security | Spring Security 7.x + JWT |
| ORM | Spring Data JPA + Hibernate |
| Database | MySQL |
| Documentation | Swagger / OpenAPI 3 |
| Build Tool | Maven |
| Utilities | Lombok |

---

## Modules

| Module | Description |
|---|---|
| Authentication | JWT-based login, BCrypt hashing, Role-based access |
| Academic Structure | Department → Program → Batch → Semester → Section |
| Student Management | CRUD, auto enrollment number, guardian info |
| Faculty Management | CRUD, auto employee code, HOD validation |
| Subject & Offering | Subject catalog + Faculty-Section-Subject mapping |
| Attendance | Session-based marking, percentage calculation |
| Exam & Marks | Internal/External exams, lock/publish flow |
| Notice System | Audience-based notice filtering |
| Library | Book issue/return, fine calculation |
| Fee Management | Fee structure, invoice generation, payment tracking |
| Timetable | Weekly schedule with conflict detection |
| Assignment | Create, submit, grade assignments |
| Reports & Analytics | Admin, Faculty, Student dashboards |

---

## Architecture

```
Controller Layer
      ↓
Service Layer (Business Logic + Validation)
      ↓
Repository Layer (Spring Data JPA)
      ↓
MySQL Database
```

**Package structure** (feature-based):
```
com.kshitij.collegeerp/
├── academic/
├── auth/
├── common/
│   ├── exception/
│   └── response/
├── config/
├── models/
│   ├── assignment/
│   ├── attendance/
│   ├── exam/
│   ├── faculty/
│   ├── fee/
│   ├── library/
│   ├── notice/
│   ├── reports/
│   ├── student/
│   ├── subject/
│   └── timetable/
├── security/
└── CollegeErpApplication.java
```

---

## Roles

| Role | Access |
|---|---|
| ADMIN | Full access to all modules |
| FACULTY | Attendance marking, marks entry, assignment creation |
| STUDENT | View own attendance, marks, fees, assignments |
| HOD | Department-level reports and faculty management |
| ACCOUNTANT | Fee structure, invoices, payments |
| LIBRARIAN | Book management, issue/return |

---

## Key Validations

Every module has cross-entity validations — not just null checks:

- Student's Program must belong to selected Department
- Student's Batch must belong to selected Program
- Student's Semester must belong to selected Batch
- Student's Section must belong to selected Semester
- Subject Offering's subject must match the Section's program and semester
- Only the assigned Faculty can mark attendance or enter marks for their subject
- A Department can have only one active HOD
- Timetable checks Room, Faculty, and Section conflicts simultaneously
- Exam marks cannot exceed defined max marks
- Fee payment cannot exceed pending amount
- Book cannot be issued if no copies are available

---

## Getting Started

### Prerequisites

- Java 17+
- MySQL 8+
- Maven 3.8+

### Setup

**1. Clone the repository:**
```bash
git clone https://github.com/kshitij-workshop/college-erp-backend.git
cd college-erp-backend
```

**2. Create MySQL database:**
```sql
CREATE DATABASE college_erp_db;
```

**3. Configure environment:**

Copy `application-example.yml` to `application.yml` and fill in your values:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/college_erp_db
    username: YOUR_DB_USERNAME
    password: YOUR_DB_PASSWORD

jwt:
  secret: YOUR_JWT_SECRET_KEY
  access-token-expiry: 900000
  refresh-token-expiry: 604800000
```

Generate a JWT secret:
```bash
openssl rand -hex 32
```

**4. Run the application:**
```bash
mvn spring-boot:run
```

**5. Access Swagger UI:**
```
http://localhost:8080/swagger-ui/index.html
```

---

## API Documentation

Full API documentation is available via Swagger UI at `/swagger-ui/index.html` after running the application.

### Authentication Flow

```
POST /api/auth/register   → Register a new user
POST /api/auth/login      → Login and receive JWT tokens
```

All protected endpoints require:
```
Authorization: Bearer <accessToken>
```

### Key Endpoints

```
# Academic Structure
POST   /api/departments
POST   /api/programs
POST   /api/batches
POST   /api/semesters
POST   /api/sections

# Student & Faculty
POST   /api/students
POST   /api/faculty

# Subject & Offering
POST   /api/subjects
POST   /api/subject-offerings

# Attendance
POST   /api/attendance/mark
GET    /api/attendance/student/{id}/percentage

# Exam & Marks
POST   /api/exams
POST   /api/marks
PATCH  /api/exams/{id}/lock
PATCH  /api/exams/{id}/publish

# Fee Management
POST   /api/fees/structures
POST   /api/fees/invoices/generate
POST   /api/fees/payments

# Library
POST   /api/library/books
POST   /api/library/issue
PATCH  /api/library/return/{issueId}

# Timetable
POST   /api/timetable/rooms
POST   /api/timetable/time-slots
POST   /api/timetable/entries

# Assignments
POST   /api/assignments
POST   /api/assignments/submit
PATCH  /api/assignments/submissions/{id}/grade

# Reports
GET    /api/reports/admin/dashboard
GET    /api/reports/student/{id}/dashboard
GET    /api/reports/faculty/{id}/dashboard
```

---

## Database Schema

The system uses 17+ interconnected tables with proper foreign key relationships and constraints:

```
users               refresh_tokens
departments         programs
batches             semesters
sections            students
faculties           subjects
subject_offerings   attendance_sessions
attendance_records  exams
marks               notices
books               book_issues
fee_structures      fee_invoices
fee_payments        rooms
time_slots          timetable_entries
assignments         assignment_submissions
```

---

## Security

- Stateless JWT authentication (no server-side sessions)
- BCrypt password hashing (strength 10)
- Role-based method-level authorization via `@PreAuthorize`
- CSRF disabled (REST API)
- Refresh token stored in database with expiry

---

## Project Status

| Phase | Status |
|---|---|
| Backend — All Modules | ✅ Complete |
| Swagger Documentation | ✅ Complete |
| Frontend (React + TypeScript) | 🔄 In Progress |

---

## Author

**Kshitij**
B.Tech CSE — GEC Sheikhpura, Bihar (BEU)

- GitHub: [@kshitij-workshop](https://github.com/kshitij-workshop)
- LinkedIn: [kshitij-cse](https://linkedin.com/in/kshitij-cse)
- X: [@_kshitiij](https://x.com/_kshitiij)
- LeetCode: [@_kshitij](https://leetcode.com/u/_kshitij)

---

## Acknowledgements

Special thanks to **Navin Reddy (Telusko)** and **Hyder Abbas** — their Spring Boot content made this architecture possible.