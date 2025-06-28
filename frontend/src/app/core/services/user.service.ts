import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface UserProfileResponse {
  id: number;
  username: string;
  email: string;
  bio?: string;
  avatarUrl?: string;
  githubUsername?: string;
  techStack: string[];
  role: string;
  joinedAt: string | number[];
  updatedAt: string | number[];
  isActive: boolean;
}

export interface UpdateUserRequest {
  username?: string;
  email?: string;
  bio?: string;
  avatarUrl?: string;
  githubUsername?: string;
  techStack?: string[];
}

export interface UserProfile {
  id: number;
  username: string;
  email: string;
  fullName: string;
  bio: string;
  avatar: string;
  location?: string;
  website?: string;
  githubUsername?: string;
  twitterUsername?: string;
  linkedinUsername?: string;
  joinDate: Date;
  techStack: string[];
  role: string;
  isActive: boolean;
}

export interface UserStats {
  totalSnippets: number;
  totalBlogs: number;
  totalProjects: number;
  totalViews: number;
  totalLikes: number;
  followersCount: number;
  followingCount: number;
}

export interface UserPreferences {
  emailNotifications: boolean;
  pushNotifications: boolean;
  weeklyDigest: boolean;
  marketingEmails: boolean;
  theme: 'light' | 'dark' | 'system';
  language: string;
  timezone: string;
  profileVisibility: 'public' | 'private';
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  private parseDate(dateInput: string | number[] | any): Date {
    // Handle null/undefined
    if (!dateInput) {
      return new Date();
    }

    // If it's already a valid date string, try parsing it
    if (typeof dateInput === 'string') {
      const parsed = new Date(dateInput);
      if (!isNaN(parsed.getTime())) {
        return parsed;
      }
    }

    // Handle LocalDateTime array format: [year, month, day, hour, minute, second, nanosecond]
    if (Array.isArray(dateInput)) {
      const [year, month, day, hour = 0, minute = 0, second = 0] = dateInput;
      // Note: JavaScript Date months are 0-indexed, so subtract 1 from month
      return new Date(year, month - 1, day, hour, minute, second);
    }

    // Fallback to current date if parsing fails
    console.warn('Unable to parse date:', dateInput);
    return new Date();
  }

  // Convert backend response to frontend model
  convertToUserProfile(userResponse: UserProfileResponse): UserProfile {
    return {
      id: userResponse.id,
      username: userResponse.username,
      email: userResponse.email,
      fullName: userResponse.username, // Use username as fullName if not provided separately
      bio: userResponse.bio || '',
      avatar: this.getFullAvatarUrl(userResponse.avatarUrl),
      githubUsername: userResponse.githubUsername,
      joinDate: this.parseDate(userResponse.joinedAt),
      techStack: userResponse.techStack || [],
      role: userResponse.role,
      isActive: userResponse.isActive,
      // These fields might not be available from backend initially
      location: '',
      website: '',
      twitterUsername: '',
      linkedinUsername: ''
    };
  }

  // Convert relative avatar URL to full URL
  private getFullAvatarUrl(avatarUrl?: string): string {
    if (!avatarUrl) {
      return '/assets/default-avatar.png';
    }
    
    if (avatarUrl.startsWith('http') || avatarUrl.startsWith('/assets/')) {
      return avatarUrl; // Already a full URL or local asset
    }
    
    // Handle the case where backend returns /api/upload/files/...
    if (avatarUrl.startsWith('/api/upload/')) {
      // Extract the path after /api/
      const pathAfterApi = avatarUrl.substring(4); // Remove /api
      return `${environment.apiUrl}${pathAfterApi}`;
    }
    
    // Remove leading slash if present to avoid double slashes
    const cleanPath = avatarUrl.startsWith('/') ? avatarUrl.slice(1) : avatarUrl;
    return `${environment.apiUrl}/${cleanPath}`;
  }

  // Get current user profile
  getCurrentUserProfile(): Observable<UserProfile> {
    return this.http.get<UserProfileResponse>(`${this.apiUrl}/me`).pipe(
      map(response => this.convertToUserProfile(response))
    );
  }

  // Get user profile by ID
  getUserProfile(userId: number): Observable<UserProfile> {
    return this.http.get<UserProfileResponse>(`${this.apiUrl}/${userId}`).pipe(
      map(response => this.convertToUserProfile(response))
    );
  }

  // Update current user profile
  updateCurrentUserProfile(updateData: UpdateUserRequest): Observable<UserProfile> {
    return this.http.put<UserProfileResponse>(`${this.apiUrl}/me`, updateData).pipe(
      map(response => this.convertToUserProfile(response))
    );
  }

  // Get user stats (now using real endpoint)
  getUserStats(userId?: number): Observable<UserStats> {
    const endpoint = userId ? `${this.apiUrl}/${userId}/stats` : `${this.apiUrl}/me/stats`;
    return this.http.get<UserStats>(endpoint);
  }

  // Get user preferences (mock for now, replace with real endpoint when available)
  getUserPreferences(): Observable<UserPreferences> {
    // TODO: Replace with real API call when backend endpoint is available
    // Return default preferences for now
    return new Observable(observer => {
      observer.next({
        emailNotifications: true,
        pushNotifications: false,
        weeklyDigest: true,
        marketingEmails: false,
        theme: 'system',
        language: 'en',
        timezone: 'UTC',
        profileVisibility: 'public'
      });
      observer.complete();
    });
  }

  // Update user preferences (mock for now)
  updateUserPreferences(preferences: Partial<UserPreferences>): Observable<UserPreferences> {
    // TODO: Replace with real API call when backend endpoint is available
    return this.getUserPreferences();
  }

  // Helper method to format dates
  getFormattedDate(date: Date | string): string {
    const d = typeof date === 'string' ? new Date(date) : date;
    return d.toLocaleDateString('en-US', { 
      year: 'numeric', 
      month: 'short', 
      day: 'numeric' 
    });
  }

  // Helper method to validate avatar URL length
  validateAvatarUrl(url: string): boolean {
    return !!url && url.length <= 500 && !url.startsWith('data:');
  }

  // Helper method to get a safe avatar URL for backend submission
  getSafeAvatarUrl(url: string | null, currentProfile?: UserProfile): string {
    // If no URL provided, use default
    if (!url) {
      return '/assets/default-avatar.png';
    }
    
    // If it's a data URL (base64), don't send it - use current or default
    if (url.startsWith('data:')) {
      return currentProfile?.avatar || '/assets/default-avatar.png';
    }
    
    // If URL is too long, use current or default
    if (url.length > 500) {
      return currentProfile?.avatar || '/assets/default-avatar.png';
    }
    
    // If it's already a full URL from our backend, keep it
    if (url.startsWith(`${environment.apiUrl}/`)) {
      return url;
    }
    
    // If it's a relative path starting with /api/upload/, handle it correctly
    if (url.startsWith('/api/upload/')) {
      // Extract the path after /api/
      const pathAfterApi = url.substring(4); // Remove /api
      return `${environment.apiUrl}${pathAfterApi}`;
    }
    
    return url;
  }
}
