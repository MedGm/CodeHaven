import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { User, LoginRequest, RegisterRequest, AuthResponse, JwtAuthenticationResponse } from '../models/auth.models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  private tokenKey = 'codehaven_token';
  private userKey = 'codehaven_user';

  constructor(private http: HttpClient) {
    this.loadStoredUser();
  }

  login(credentials: LoginRequest): Observable<JwtAuthenticationResponse> {
    return this.http.post<JwtAuthenticationResponse>(`${environment.apiUrl}/auth/login`, credentials)
      .pipe(
        tap(response => {
          // Convert the response to User object
          const user: User = {
            id: response.userId,
            username: response.username,
            email: response.email,
            role: response.role as 'USER' | 'ADMIN',
            isActive: true,
            joinedAt: new Date(),
            updatedAt: new Date()
          };
          this.setUserData(response.accessToken, user);
        })
      );
  }

  register(userData: RegisterRequest): Observable<JwtAuthenticationResponse> {
    return this.http.post<JwtAuthenticationResponse>(`${environment.apiUrl}/auth/signup`, userData)
      .pipe(
        tap(response => {
          // Convert the response to User object
          const user: User = {
            id: response.userId,
            username: response.username,
            email: response.email,
            role: response.role as 'USER' | 'ADMIN',
            isActive: true,
            joinedAt: new Date(),
            updatedAt: new Date()
          };
          this.setUserData(response.accessToken, user);
        })
      );
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
    this.currentUserSubject.next(null);
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem(this.tokenKey);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  // Method to update current user data (e.g., after profile update)
  updateCurrentUser(userData: Partial<User>): void {
    const currentUser = this.currentUserSubject.value;
    if (currentUser) {
      const updatedUser = { ...currentUser, ...userData };
      this.setUserData(this.getToken() || '', updatedUser);
    }
  }

  private setUserData(token: string, user: User): void {
    localStorage.setItem(this.tokenKey, token);
    localStorage.setItem(this.userKey, JSON.stringify(user));
    this.currentUserSubject.next(user);
  }

  private loadStoredUser(): void {
    const storedUser = localStorage.getItem(this.userKey);
    if (storedUser && this.isAuthenticated()) {
      this.currentUserSubject.next(JSON.parse(storedUser));
    }
  }
}
