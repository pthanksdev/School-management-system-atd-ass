-- ============================================================
--  School Management System — Initial Schema
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ── Users ────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    first_name  VARCHAR(100) NOT NULL,
    last_name   VARCHAR(100) NOT NULL,
    phone       VARCHAR(20),
    role        VARCHAR(20)  NOT NULL CHECK (role IN ('ADMIN','TEACHER','STUDENT','PARENT')),
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    fcm_token   TEXT,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role  ON users(role);

-- ── Academic Years ───────────────────────────────────────────
CREATE TABLE IF NOT EXISTS academic_years (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name        VARCHAR(50)  NOT NULL,
    start_date  DATE         NOT NULL,
    end_date    DATE         NOT NULL,
    is_current  BOOLEAN      NOT NULL DEFAULT FALSE
);

-- ── Departments ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS departments (
    id               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name             VARCHAR(100) NOT NULL UNIQUE,
    head_teacher_id  UUID REFERENCES users(id) ON DELETE SET NULL
);

-- ── Subjects ─────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS subjects (
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name          VARCHAR(100) NOT NULL,
    code          VARCHAR(20)  NOT NULL UNIQUE,
    department_id UUID REFERENCES departments(id) ON DELETE SET NULL
);

-- ── Classes ──────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS classes (
    id               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name             VARCHAR(100) NOT NULL,
    grade_level      INT          NOT NULL,
    academic_year_id UUID REFERENCES academic_years(id) ON DELETE RESTRICT,
    class_teacher_id UUID REFERENCES users(id) ON DELETE SET NULL
);

-- ── Class Subjects ───────────────────────────────────────────
CREATE TABLE IF NOT EXISTS class_subjects (
    id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    class_id   UUID NOT NULL REFERENCES classes(id)   ON DELETE CASCADE,
    subject_id UUID NOT NULL REFERENCES subjects(id)  ON DELETE RESTRICT,
    teacher_id UUID NOT NULL REFERENCES users(id)     ON DELETE RESTRICT,
    schedule   TEXT,
    UNIQUE (class_id, subject_id)
);
CREATE INDEX IF NOT EXISTS idx_class_subjects_teacher ON class_subjects(teacher_id);
CREATE INDEX IF NOT EXISTS idx_class_subjects_class   ON class_subjects(class_id);

-- ── Teachers ─────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS teachers (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    employee_number VARCHAR(50) NOT NULL UNIQUE,
    department_id   UUID REFERENCES departments(id) ON DELETE SET NULL,
    specialization  VARCHAR(100),
    joined_at       DATE
);

-- ── Students ─────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS students (
    id               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id          UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    admission_number VARCHAR(50) NOT NULL UNIQUE,
    class_id         UUID REFERENCES classes(id) ON DELETE SET NULL,
    parent_id        UUID REFERENCES users(id)   ON DELETE SET NULL,
    date_of_birth    DATE,
    gender           VARCHAR(10) CHECK (gender IN ('MALE','FEMALE')),
    address          TEXT,
    enrolled_at      DATE
);
CREATE INDEX IF NOT EXISTS idx_students_class  ON students(class_id);
CREATE INDEX IF NOT EXISTS idx_students_parent ON students(parent_id);

-- ── Attendance Records ───────────────────────────────────────
CREATE TABLE IF NOT EXISTS attendance_records (
    id               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    student_id       UUID NOT NULL REFERENCES students(id)       ON DELETE CASCADE,
    class_subject_id UUID NOT NULL REFERENCES class_subjects(id) ON DELETE CASCADE,
    date             DATE NOT NULL,
    status           VARCHAR(10) NOT NULL CHECK (status IN ('PRESENT','ABSENT','LATE','EXCUSED')),
    marked_by        UUID REFERENCES users(id) ON DELETE SET NULL,
    note             TEXT,
    marked_at        TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (student_id, class_subject_id, date)
);
CREATE INDEX IF NOT EXISTS idx_attendance_student ON attendance_records(student_id);
CREATE INDEX IF NOT EXISTS idx_attendance_date    ON attendance_records(date);
CREATE INDEX IF NOT EXISTS idx_attendance_cs      ON attendance_records(class_subject_id);

-- ── Assignments ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS assignments (
    id                    UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title                 VARCHAR(255) NOT NULL,
    description           TEXT,
    class_subject_id      UUID NOT NULL REFERENCES class_subjects(id) ON DELETE CASCADE,
    created_by            UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    due_date              TIMESTAMP NOT NULL,
    max_score             INT       NOT NULL DEFAULT 100,
    allow_late_submission BOOLEAN   NOT NULL DEFAULT FALSE,
    attachment_url        TEXT,
    status                VARCHAR(20) NOT NULL DEFAULT 'DRAFT'
                              CHECK (status IN ('DRAFT','PUBLISHED','CLOSED')),
    created_at            TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_assignments_cs      ON assignments(class_subject_id);
CREATE INDEX IF NOT EXISTS idx_assignments_status  ON assignments(status);
CREATE INDEX IF NOT EXISTS idx_assignments_teacher ON assignments(created_by);

-- ── Submissions ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS submissions (
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    assignment_id UUID NOT NULL REFERENCES assignments(id) ON DELETE CASCADE,
    student_id    UUID NOT NULL REFERENCES students(id)    ON DELETE CASCADE,
    file_url      TEXT,
    text_content  TEXT,
    submitted_at  TIMESTAMP,
    is_late       BOOLEAN  NOT NULL DEFAULT FALSE,
    score         INT,
    feedback      TEXT,
    graded_by     UUID REFERENCES users(id) ON DELETE SET NULL,
    graded_at     TIMESTAMP,
    status        VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                      CHECK (status IN ('PENDING','SUBMITTED','GRADED','RETURNED')),
    UNIQUE (assignment_id, student_id)
);
CREATE INDEX IF NOT EXISTS idx_submissions_assignment ON submissions(assignment_id);
CREATE INDEX IF NOT EXISTS idx_submissions_student    ON submissions(student_id);
CREATE INDEX IF NOT EXISTS idx_submissions_status     ON submissions(status);

-- ── Notifications ────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS notifications (
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    recipient_id UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title        VARCHAR(255) NOT NULL,
    body         TEXT         NOT NULL,
    type         VARCHAR(20)  NOT NULL CHECK (type IN ('ATTENDANCE','ASSIGNMENT','GRADE','GENERAL')),
    is_read      BOOLEAN      NOT NULL DEFAULT FALSE,
    reference_id UUID,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_notifications_recipient ON notifications(recipient_id);
CREATE INDEX IF NOT EXISTS idx_notifications_unread    ON notifications(recipient_id, is_read);

-- ── Seed: Default Admin ───────────────────────────────────────
-- Password: Admin@12345  (BCrypt hash)
INSERT INTO users (id, email, password, first_name, last_name, role, is_active)
VALUES (
    uuid_generate_v4(),
    'admin@school.com',
    '$2a$12$LjNfyCHlwB8ZkCtCAhVQIOOVr/UhgoBLV4hkFwMCl0OA5v1E.CiVi',
    'System', 'Admin', 'ADMIN', TRUE
) ON CONFLICT (email) DO NOTHING;
