export type AttendanceStatus = "PRESENT" | "ABSENT" | "LATE" | "EXCUSED";

export interface AttendanceRecord {
  id: string;
  studentId: string;
  date: string;
  status: AttendanceStatus;
  notes?: string;
}

export interface MarkAttendanceRequest {
  studentId: string;
  date: string;
  status: AttendanceStatus;
}

export interface AttendanceSummary {
  studentId: string;
  present: number;
  absent: number;
  late: number;
  excused: number;
}
