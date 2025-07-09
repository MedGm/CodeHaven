import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap, switchMap, catchError, of } from 'rxjs';
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
        switchMap(response => {
          // Store the token first
          localStorage.setItem(this.tokenKey, response.accessToken);
          
          // Create basic user object from auth response
          const basicUser: User = {
            id: response.userId,
            username: response.username,
            email: response.email,
            role: response.role as 'USER' | 'ADMIN',
            isActive: true,
            joinedAt: new Date(),
            updatedAt: new Date()
          };
          
          // Set basic user data first
          this.setUserData(response.accessToken, basicUser);
          
          // Fetch complete user profile and return the login response
          return this.fetchAndUpdateUserProfile().pipe(
            switchMap(() => of(response)), // Return the original login response after profile update
            catchError((error) => {
              console.warn('Failed to fetch complete user profile after login:', error);
              // If it's an auth error, the token might be invalid immediately
              if (error.status === 401 || error.status === 403) {
                this.logout();
                throw error; // Re-throw auth errors
              }
              // For other errors, still return the login response with basic user data
              return of(response);
            })
          );
        })
      );
  }

  register(userData: RegisterRequest): Observable<JwtAuthenticationResponse> {
    return this.http.post<JwtAuthenticationResponse>(`${environment.apiUrl}/auth/signup`, userData)
      .pipe(
        switchMap(response => {
          // Store the token first
          localStorage.setItem(this.tokenKey, response.accessToken);
          
          // Create basic user object from auth response
          const basicUser: User = {
            id: response.userId,
            username: response.username,
            email: response.email,
            role: response.role as 'USER' | 'ADMIN',
            isActive: true,
            joinedAt: new Date(),
            updatedAt: new Date()
          };
          
          // Set basic user data first
          this.setUserData(response.accessToken, basicUser);
          
          // Fetch complete user profile and return the register response
          return this.fetchAndUpdateUserProfile().pipe(
            switchMap(() => of(response)), // Return the original register response after profile update
            catchError((error) => {
              console.warn('Failed to fetch complete user profile after registration:', error);
              // If it's an auth error, the token might be invalid immediately
              if (error.status === 401 || error.status === 403) {
                this.logout();
                throw error; // Re-throw auth errors
              }
              // For other errors, still return the register response with basic user data
              return of(response);
            })
          );
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

  // Fetch complete user profile from backend
  private fetchAndUpdateUserProfile(): Observable<any> {
    return this.http.get<any>(`${environment.apiUrl}/users/me`).pipe(
      tap(userProfile => {
        console.log('Fetched user profile:', userProfile);
        const currentUser = this.currentUserSubject.value;
        if (currentUser) {
          // Update current user with complete profile data
          const updatedUser: User = {
            ...currentUser,
            avatarUrl: userProfile.avatarUrl,
            bio: userProfile.bio,
            githubUsername: userProfile.githubUsername
          };
          console.log('Updating user with profile data, avatar:', updatedUser.avatarUrl);
          this.setUserData(this.getToken() || '', updatedUser);
        }
      })
    );
  }

  private setUserData(token: string, user: User): void {
    console.log('Setting user data:', user.username, 'Avatar:', user.avatarUrl);
    localStorage.setItem(this.tokenKey, token);
    localStorage.setItem(this.userKey, JSON.stringify(user));
    this.currentUserSubject.next(user);
  }

  private loadStoredUser(): void {
    const storedUser = localStorage.getItem(this.userKey);
    const token = localStorage.getItem(this.tokenKey);
    
    if (storedUser && token) {
      // Set the stored user first
      const user = JSON.parse(storedUser);
      this.currentUserSubject.next(user);
      
      // Fetch fresh user data from backend to ensure avatar and other data is up to date
      this.fetchAndUpdateUserProfile().subscribe({
        next: () => {
          console.log('User profile refreshed on app load');
        },
        error: (error) => {
          console.warn('Failed to refresh user profile on app load:', error);
          // If the token is expired or invalid, logout
          if (error.status === 401 || error.status === 403) {
            console.log('Token expired, logging out');
            this.logout();
          }
          // For other errors, keep the stored user data
        }
      });
    }
  }
}
