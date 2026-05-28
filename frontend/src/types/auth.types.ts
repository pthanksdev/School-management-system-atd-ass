export type Role = "ADMIN" | "TEACHER" | "STUDENT" | "PARENT";

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  refreshToken: string;
  user: UserDTO;
}
