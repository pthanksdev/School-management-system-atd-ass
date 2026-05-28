import { Role } from './auth.types';

export interface UserDTO {
  id: string;
  username: string;
  email: string;
  role: Role;
  firstName?: string;
  lastName?: string;
  avatar?: string;
}
