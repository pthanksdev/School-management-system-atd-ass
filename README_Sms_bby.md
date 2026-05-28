# 🏫 School Management System

A modern web-based school management system focused on **attendance tracking** and **assignment management** for secondary schools (High School, Ages 14–18).

-----

## 🧱 Tech Stack

|Layer           |Technology                              |
|----------------|----------------------------------------|
|Frontend        |Next.js 14+ (App Router)                |
|Language        |TypeScript                              |
|Styling         |Tailwind CSS                            |
|HTTP Client     |Axios (typed)                           |
|State Management|Zustand / TanStack Query                |
|Forms           |React Hook Form + Zod                   |
|Backend         |Spring Boot (Java)                      |
|Database        |PostgreSQL                              |
|Auth            |Spring Security + JWT (httpOnly cookies)|
|Notifications   |Firebase Cloud Messaging                |
|File Storage    |AWS S3 / Cloudinary                     |

-----

## 👥 User Roles

|Role       |Description                                               |
|-----------|----------------------------------------------------------|
|**Admin**  |Manages school setup, users, classes, timetable, reports  |
|**Teacher**|Marks attendance, creates and grades assignments          |
|**Student**|Views schedule, submits assignments, tracks grades        |
|**Parent** |Receives alerts, monitors child’s attendance & assignments|

-----

## 🔐 Role Access Matrix

|Feature            |Admin|Teacher      |Student|Parent   |
|-------------------|-----|-------------|-------|---------|
|Mark Attendance    |✅    |✅            |❌      |❌        |
|View Own Attendance|❌    |❌            |✅      |✅        |
|View All Attendance|✅    |✅ (own class)|❌      |❌        |
|Create Assignment  |❌    |✅            |❌      |❌        |
|Submit Assignment  |❌    |❌            |✅      |❌        |
|Grade Submission   |❌    |✅            |❌      |❌        |
|View Assignments   |✅    |✅            |✅      |✅        |
|Manage Users       |✅    |❌            |❌      |❌        |
|View Reports       |✅    |✅ (own)      |❌      |✅ (child)|

-----

## 🗄️ Database Schema

```sql
-- Users (base for all roles)
users
  id UUID PK
  email VARCHAR UNIQUE
  password VARCHAR
  first_name VARCHAR
  last_name VARCHAR
  phone VARCHAR
  role ENUM(ADMIN, TEACHER, STUDENT, PARENT)
  is_active BOOLEAN
  created_at TIMESTAMP
  updated_at TIMESTAMP

-- Academic Years
academic_years
  id UUID PK
  name VARCHAR              -- e.g., "2025/2026"
  start_date DATE
  end_date DATE
  is_current BOOLEAN

-- Departments
departments
  id UUID PK
  name VARCHAR
  head_teacher_id UUID FK(users)

-- Subjects
subjects
  id UUID PK
  name VARCHAR
  code VARCHAR UNIQUE
  department_id UUID FK(departments)

-- Classes/Streams
classes
  id UUID PK
  name VARCHAR              -- e.g., "Form 3A"
  grade_level INT           -- 9–12
  academic_year_id UUID FK(academic_years)
  class_teacher_id UUID FK(users)

-- Teacher-Subject-Class assignment
class_subjects
  id UUID PK
  class_id UUID FK(classes)
  subject_id UUID FK(subjects)
  teacher_id UUID FK(users)
  schedule JSON             -- days/times

-- Students
students
  id UUID PK
  user_id UUID FK(users)
  admission_number VARCHAR UNIQUE
  class_id UUID FK(classes)
  parent_id UUID FK(users)
  date_of_birth DATE
  gender ENUM(MALE, FEMALE)
  address TEXT
  enrolled_at DATE

-- Teachers
teachers
  id UUID PK
  user_id UUID FK(users)
  employee_number VARCHAR UNIQUE
  department_id UUID FK(departments)
  specialization VARCHAR
  joined_at DATE

-- Attendance
attendance_records
  id UUID PK
  student_id UUID FK(students)
  class_subject_id UUID FK(class_subjects)
  date DATE
  status ENUM(PRESENT, ABSENT, LATE, EXCUSED)
  marked_by UUID FK(users)
  note TEXT
  marked_at TIMESTAMP

-- Assignments
assignments
  id UUID PK
  title VARCHAR
  description TEXT
  class_subject_id UUID FK(class_subjects)
  created_by UUID FK(users)
  due_date TIMESTAMP
  max_score INT
  allow_late_submission BOOLEAN
  attachment_url VARCHAR
  status ENUM(DRAFT, PUBLISHED, CLOSED)
  created_at TIMESTAMP
  updated_at TIMESTAMP

-- Submissions
submissions
  id UUID PK
  assignment_id UUID FK(assignments)
  student_id UUID FK(students)
  file_url VARCHAR
  text_content TEXT
  submitted_at TIMESTAMP
  is_late BOOLEAN
  score INT
  feedback TEXT
  graded_by UUID FK(users)
  graded_at TIMESTAMP
  status ENUM(PENDING, SUBMITTED, GRADED, RETURNED)

-- Notifications
notifications
  id UUID PK
  user_id UUID FK(users)
  title VARCHAR
  message TEXT
  type ENUM(ATTENDANCE, ASSIGNMENT, GRADE, GENERAL)
  is_read BOOLEAN
  created_at TIMESTAMP
```

-----

## ☕ Spring Boot Project Structure

```
school-management/
├── src/
│   └── main/
│       ├── java/com/school/
│       │   ├── SchoolManagementApplication.java
│       │   │
│       │   ├── config/
│       │   │   ├── SecurityConfig.java
│       │   │   ├── JwtConfig.java
│       │   │   ├── CorsConfig.java
│       │   │   └── FirebaseConfig.java
│       │   │
│       │   ├── common/
│       │   │   ├── enums/
│       │   │   │   ├── Role.java
│       │   │   │   ├── AttendanceStatus.java
│       │   │   │   ├── SubmissionStatus.java
│       │   │   │   └── AssignmentStatus.java
│       │   │   ├── exception/
│       │   │   │   ├── GlobalExceptionHandler.java
│       │   │   │   ├── ResourceNotFoundException.java
│       │   │   │   └── UnauthorizedException.java
│       │   │   ├── response/
│       │   │   │   └── ApiResponse.java
│       │   │   └── util/
│       │   │       ├── JwtUtil.java
│       │   │       └── DateUtil.java
│       │   │
│       │   ├── auth/
│       │   │   ├── AuthController.java
│       │   │   ├── AuthService.java
│       │   │   ├── dto/
│       │   │   │   ├── LoginRequest.java
│       │   │   │   ├── LoginResponse.java
│       │   │   │   └── RegisterRequest.java
│       │   │   └── filter/
│       │   │       └── JwtAuthFilter.java
│       │   │
│       │   ├── user/
│       │   │   ├── User.java
│       │   │   ├── UserRepository.java
│       │   │   ├── UserService.java
│       │   │   ├── UserController.java
│       │   │   └── dto/
│       │   │       ├── UserDTO.java
│       │   │       └── UpdateUserRequest.java
│       │   │
│       │   ├── student/
│       │   │   ├── Student.java
│       │   │   ├── StudentRepository.java
│       │   │   ├── StudentService.java
│       │   │   ├── StudentController.java
│       │   │   └── dto/
│       │   │       ├── StudentDTO.java
│       │   │       └── CreateStudentRequest.java
│       │   │
│       │   ├── teacher/
│       │   │   ├── Teacher.java
│       │   │   ├── TeacherRepository.java
│       │   │   ├── TeacherService.java
│       │   │   ├── TeacherController.java
│       │   │   └── dto/
│       │   │       ├── TeacherDTO.java
│       │   │       └── CreateTeacherRequest.java
│       │   │
│       │   ├── academic/
│       │   │   ├── academicyear/
│       │   │   │   ├── AcademicYear.java
│       │   │   │   ├── AcademicYearRepository.java
│       │   │   │   ├── AcademicYearService.java
│       │   │   │   └── AcademicYearController.java
│       │   │   ├── department/
│       │   │   │   ├── Department.java
│       │   │   │   ├── DepartmentRepository.java
│       │   │   │   ├── DepartmentService.java
│       │   │   │   └── DepartmentController.java
│       │   │   ├── subject/
│       │   │   │   ├── Subject.java
│       │   │   │   ├── SubjectRepository.java
│       │   │   │   ├── SubjectService.java
│       │   │   │   └── SubjectController.java
│       │   │   └── classes/
│       │   │       ├── Class.java
│       │   │       ├── ClassSubject.java
│       │   │       ├── ClassRepository.java
│       │   │       ├── ClassSubjectRepository.java
│       │   │       ├── ClassService.java
│       │   │       └── ClassController.java
│       │   │
│       │   ├── attendance/
│       │   │   ├── AttendanceRecord.java
│       │   │   ├── AttendanceRepository.java
│       │   │   ├── AttendanceService.java
│       │   │   ├── AttendanceController.java
│       │   │   └── dto/
│       │   │       ├── AttendanceDTO.java
│       │   │       ├── MarkAttendanceRequest.java
│       │   │       └── AttendanceSummaryDTO.java
│       │   │
│       │   ├── assignment/
│       │   │   ├── Assignment.java
│       │   │   ├── AssignmentRepository.java
│       │   │   ├── AssignmentService.java
│       │   │   ├── AssignmentController.java
│       │   │   ├── submission/
│       │   │   │   ├── Submission.java
│       │   │   │   ├── SubmissionRepository.java
│       │   │   │   ├── SubmissionService.java
│       │   │   │   └── SubmissionController.java
│       │   │   └── dto/
│       │   │       ├── AssignmentDTO.java
│       │   │       ├── CreateAssignmentRequest.java
│       │   │       ├── SubmissionDTO.java
│       │   │       ├── SubmitAssignmentRequest.java
│       │   │       └── GradeSubmissionRequest.java
│       │   │
│       │   └── notification/
│       │       ├── Notification.java
│       │       ├── NotificationRepository.java
│       │       ├── NotificationService.java
│       │       ├── NotificationController.java
│       │       └── FirebaseMessagingService.java
│       │
│       └── resources/
│           ├── application.properties
│           ├── application-dev.properties
│           └── application-prod.properties
│
└── pom.xml
```

-----

## 🌐 API Endpoints

Base URL: `/api/v1`

### Auth

```
POST   /auth/login
POST   /auth/logout
POST   /auth/refresh-token
POST   /auth/change-password
```

### Users

```
GET    /users
GET    /users/{id}
PUT    /users/{id}
DELETE /users/{id}
GET    /users/me
```

### Students

```
GET    /students
POST   /students
GET    /students/{id}
PUT    /students/{id}
DELETE /students/{id}
GET    /students/{id}/attendance
GET    /students/{id}/assignments
GET    /students/{id}/submissions
```

### Teachers

```
GET    /teachers
POST   /teachers
GET    /teachers/{id}
PUT    /teachers/{id}
GET    /teachers/{id}/classes
GET    /teachers/{id}/assignments
```

### Academic

```
GET    /academic-years
POST   /academic-years
GET    /academic-years/current

GET    /departments
POST   /departments
GET    /departments/{id}

GET    /subjects
POST   /subjects
GET    /subjects/{id}

GET    /classes
POST   /classes
GET    /classes/{id}
GET    /classes/{id}/students
GET    /classes/{id}/subjects
POST   /classes/{id}/subjects        ← assign subject + teacher to class
```

### Attendance

```
POST   /attendance/mark                          ← bulk mark attendance
GET    /attendance/class/{classSubjectId}        ← by class & date
GET    /attendance/student/{studentId}           ← student history
GET    /attendance/student/{studentId}/summary   ← % present/absent
PUT    /attendance/{id}                          ← correct a record
GET    /attendance/report                        ← admin report (filters)
```

### Assignments

```
GET    /assignments
POST   /assignments
GET    /assignments/{id}
PUT    /assignments/{id}
DELETE /assignments/{id}
PATCH  /assignments/{id}/publish
PATCH  /assignments/{id}/close
GET    /assignments/class/{classSubjectId}
GET    /assignments/student/{studentId}          ← student's assignments
```

### Submissions

```
POST   /submissions                              ← student submits
GET    /submissions/{id}
GET    /submissions/assignment/{assignmentId}    ← all submissions for assignment
GET    /submissions/student/{studentId}
PATCH  /submissions/{id}/grade                  ← teacher grades
```

### Notifications

```
GET    /notifications
PATCH  /notifications/{id}/read
PATCH  /notifications/read-all
DELETE /notifications/{id}
```

-----

## 💻 Next.js Project Structure

```
school-web/
├── src/
│   ├── app/
│   │   ├── layout.tsx
│   │   ├── page.tsx                            ← redirects to login
│   │   ├── (auth)/
│   │   │   └── login/
│   │   │       └── page.tsx
│   │   └── (dashboard)/
│   │       ├── layout.tsx                      ← protected layout
│   │       ├── admin/
│   │       │   ├── page.tsx                    ← admin dashboard
│   │       │   ├── students/
│   │       │   │   ├── page.tsx
│   │       │   │   ├── [id]/page.tsx
│   │       │   │   └── new/page.tsx
│   │       │   ├── teachers/
│   │       │   │   ├── page.tsx
│   │       │   │   ├── [id]/page.tsx
│   │       │   │   └── new/page.tsx
│   │       │   ├── classes/
│   │       │   │   ├── page.tsx
│   │       │   │   └── [id]/page.tsx
│   │       │   ├── subjects/
│   │       │   │   └── page.tsx
│   │       │   └── reports/
│   │       │       ├── attendance/page.tsx
│   │       │       └── assignments/page.tsx
│   │       ├── teacher/
│   │       │   ├── page.tsx                    ← teacher dashboard
│   │       │   ├── attendance/
│   │       │   │   ├── page.tsx                ← select class to mark
│   │       │   │   └── [classSubjectId]/
│   │       │   │       └── page.tsx            ← mark attendance
│   │       │   └── assignments/
│   │       │       ├── page.tsx
│   │       │       ├── new/page.tsx
│   │       │       ├── [id]/page.tsx
│   │       │       └── [id]/submissions/
│   │       │           └── page.tsx            ← view & grade submissions
│   │       ├── student/
│   │       │   ├── page.tsx                    ← student dashboard
│   │       │   ├── attendance/
│   │       │   │   └── page.tsx                ← own attendance history
│   │       │   └── assignments/
│   │       │       ├── page.tsx                ← list of assignments
│   │       │       └── [id]/page.tsx           ← view & submit
│   │       └── parent/
│   │           ├── page.tsx                    ← parent dashboard
│   │           ├── attendance/
│   │           │   └── page.tsx                ← child's attendance
│   │           └── assignments/
│   │               └── page.tsx                ← child's assignments
│   │
│   ├── components/
│   │   ├── ui/                                 ← base UI components
│   │   │   ├── Button.tsx
│   │   │   ├── Input.tsx
│   │   │   ├── Modal.tsx
│   │   │   ├── Table.tsx
│   │   │   ├── Badge.tsx
│   │   │   ├── Card.tsx
│   │   │   └── Spinner.tsx
│   │   ├── layout/
│   │   │   ├── Sidebar.tsx
│   │   │   ├── Navbar.tsx
│   │   │   └── DashboardLayout.tsx
│   │   ├── attendance/
│   │   │   ├── AttendanceTable.tsx
│   │   │   ├── AttendanceStatusBadge.tsx
│   │   │   ├── MarkAttendanceForm.tsx
│   │   │   └── AttendanceSummaryCard.tsx
│   │   ├── assignments/
│   │   │   ├── AssignmentCard.tsx
│   │   │   ├── AssignmentForm.tsx
│   │   │   ├── SubmissionList.tsx
│   │   │   ├── SubmitAssignmentForm.tsx
│   │   │   └── GradeForm.tsx
│   │   └── notifications/
│   │       ├── NotificationBell.tsx
│   │       └── NotificationList.tsx
│   │
│   ├── services/                               ← all API calls
│   │   ├── api.ts                              ← axios instance
│   │   ├── auth.service.ts
│   │   ├── student.service.ts
│   │   ├── teacher.service.ts
│   │   ├── attendance.service.ts
│   │   ├── assignment.service.ts
│   │   ├── submission.service.ts
│   │   └── notification.service.ts
│   │
│   ├── hooks/
│   │   ├── useAuth.ts
│   │   ├── useAttendance.ts
│   │   ├── useAssignments.ts
│   │   └── useNotifications.ts
│   │
│   ├── store/                                  ← Zustand stores
│   │   ├── auth.store.ts
│   │   └── notification.store.ts
│   │
│   ├── types/
│   │   ├── index.ts
│   │   ├── auth.types.ts
│   │   ├── user.types.ts
│   │   ├── student.types.ts
│   │   ├── teacher.types.ts
│   │   ├── attendance.types.ts
│   │   ├── assignment.types.ts
│   │   └── notification.types.ts
│   │
│   ├── lib/
│   │   ├── axios.ts                            ← configured axios instance
│   │   ├── auth.ts                             ← token helpers
│   │   └── utils.ts
│   │
│   └── middleware.ts                           ← route protection by role
│
├── public/
├── .env.local
├── next.config.ts
├── tailwind.config.ts
└── tsconfig.json
```

-----

## 📦 TypeScript Types

```typescript
// types/auth.types.ts
export type Role = "ADMIN" | "TEACHER" | "STUDENT" | "PARENT"

export interface LoginRequest {
  email: string
  password: string
}

export interface AuthResponse {
  token: string
  refreshToken: string
  user: UserDTO
}

// types/user.types.ts
export interface UserDTO {
  id: string
  email: string
  firstName: string
  lastName: string
  phone?: string
  role: Role
  isActive: boolean
}

// types/attendance.types.ts
export type AttendanceStatus = "PRESENT" | "ABSENT" | "LATE" | "EXCUSED"

export interface AttendanceRecord {
  id: string
  studentId: string
  studentName: string
  classSubjectId: string
  date: string
  status: AttendanceStatus
  note?: string
  markedAt: string
}

export interface MarkAttendanceRequest {
  classSubjectId: string
  date: string
  records: {
    studentId: string
    status: AttendanceStatus
    note?: string
  }[]
}

export interface AttendanceSummary {
  studentId: string
  totalDays: number
  present: number
  absent: number
  late: number
  excused: number
  attendancePercentage: number
}

// types/assignment.types.ts
export type AssignmentStatus = "DRAFT" | "PUBLISHED" | "CLOSED"
export type SubmissionStatus = "PENDING" | "SUBMITTED" | "GRADED" | "RETURNED"

export interface Assignment {
  id: string
  title: string
  description: string
  classSubjectId: string
  subjectName: string
  className: string
  createdBy: string
  dueDate: string
  maxScore: number
  allowLateSubmission: boolean
  attachmentUrl?: string
  status: AssignmentStatus
  createdAt: string
}

export interface CreateAssignmentRequest {
  title: string
  description: string
  classSubjectId: string
  dueDate: string
  maxScore: number
  allowLateSubmission: boolean
  attachmentUrl?: string
}

export interface Submission {
  id: string
  assignmentId: string
  studentId: string
  studentName: string
  fileUrl?: string
  textContent?: string
  submittedAt: string
  isLate: boolean
  score?: number
  feedback?: string
  status: SubmissionStatus
}

export interface GradeSubmissionRequest {
  score: number
  feedback?: string
}

// types/notification.types.ts
export type NotificationType = "ATTENDANCE" | "ASSIGNMENT" | "GRADE" | "GENERAL"

export interface Notification {
  id: string
  userId: string
  title: string
  message: string
  type: NotificationType
  isRead: boolean
  createdAt: string
}
```

-----

## ⚙️ Environment Variables

### Next.js (`school-web/.env.local`)

```env
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080/api/v1
NEXT_PUBLIC_FIREBASE_API_KEY=your_firebase_api_key
NEXT_PUBLIC_FIREBASE_PROJECT_ID=your_project_id
NEXT_PUBLIC_FIREBASE_MESSAGING_SENDER_ID=your_sender_id
NEXT_PUBLIC_FIREBASE_APP_ID=your_app_id
```

### Spring Boot (`application-dev.properties`)

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/school_db
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password
spring.jpa.hibernate.ddl-auto=update

jwt.secret=your_jwt_secret
jwt.expiration=86400000
jwt.refresh-expiration=604800000

firebase.config.path=src/main/resources/firebase-service-account.json

cloud.aws.s3.bucket=your_bucket_name
cloud.aws.credentials.access-key=your_access_key
cloud.aws.credentials.secret-key=your_secret_key
cloud.aws.region.static=us-east-1
```

-----

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Node.js 18+
- PostgreSQL 15+
- Maven

### Backend

```bash
cd school-management
mvn clean install
mvn spring-boot:run
# Runs on http://localhost:8080
```

### Frontend

```bash
cd school-web
npm install
npm run dev
# Runs on http://localhost:3000
```

-----

## 📌 Notes

- JWT tokens are stored in **httpOnly cookies** for security
- Next.js `middleware.ts` handles **route protection by role**
- Attendance and assignment data are linked — dashboards show combined student insights
- Firebase FCM handles real-time push notifications to parents on absence events