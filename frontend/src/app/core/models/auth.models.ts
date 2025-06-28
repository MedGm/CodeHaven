export interface User {
  id?: number;
  username: string;
  email: string;
  password?: string;
  role: 'USER' | 'ADMIN';
  avatarUrl?: string;
  bio?: string;
  githubUsername?: string;
  isActive: boolean;
  joinedAt: Date;
  updatedAt: Date;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

// Backend response format
export interface JwtAuthenticationResponse {
  accessToken: string;
  tokenType: string;
  userId: number;
  username: string;
  email: string;
  role: string;
}

// Legacy format (keeping for compatibility)
export interface AuthResponse {
  success: boolean;
  message: string;
  data: {
    token: string;
    user: User;
  };
}
